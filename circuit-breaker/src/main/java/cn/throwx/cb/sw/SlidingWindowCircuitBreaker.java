package cn.throwx.cb.sw;

import cn.throwx.cb.CircuitBreakerResource;
import cn.throwx.cb.CircuitBreakerResourceManager;
import cn.throwx.cb.CircuitBreakerStatus;
import cn.throwx.cb.CircuitBreakerStatusMonitor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 17:14
 */
public class SlidingWindowCircuitBreaker {

    /**
     * 失败百分比阈值
     */
    private final long errorPercentThreshold;

    /**
     * 熔断等待窗口
     */
    private final long resetTimeout;

    private AtomicReference<CircuitBreakerStatus> status;

    private final ThreadPoolExecutor executor;

    private final String circuitBreakerName;

    /**
     * 最后一次调用失败的时间戳
     */
    private long lastFailureTime;

    /**
     * 执行超时上限,单位毫秒
     */
    private final long executionTimeout;

    /**
     * 滑动窗口监视器
     */
    private final SlidingWindowMonitor slidingWindowMonitor;

    public SlidingWindowCircuitBreaker(String resourceName,
                                       long errorPercentThreshold,
                                       long resetTimeout) {
        CircuitBreakerResource resource = CircuitBreakerResourceManager.X.get(resourceName);
        this.circuitBreakerName = "SlidingWindowCircuitBreaker-" + resourceName;
        this.executor = resource.getExecutor();
        this.executionTimeout = resource.getTimeout();
        this.errorPercentThreshold = errorPercentThreshold;
        this.resetTimeout = resetTimeout;
        this.slidingWindowMonitor = new SlidingWindowMonitor();
        reset();
    }

    public void reset() {
        CircuitBreakerStatusMonitor.X.reset(this.circuitBreakerName);
        this.status = new AtomicReference<>(CircuitBreakerStatus.CLOSED);
        this.lastFailureTime = -1L;
    }

    @SuppressWarnings("unchecked")
    public <T> T call(Supplier<T> supplier) {
        return call(supplier, (Fallback<T>) Fallback.F);
    }

    public <T> T call(Supplier<T> supplier, Fallback<T> fallback) {
        try {
            if (shouldAllowExecution()) {
                slidingWindowMonitor.incrementTotal();
                Future<T> future = this.executor.submit(warp(supplier));
                T result = future.get(executionTimeout, TimeUnit.MILLISECONDS);
                markSuccess();
                return result;
            }
        } catch (RejectedExecutionException ree) {
            markReject();
        } catch (Exception e) {
            markFailure();
        }
        return fallback.fallback();
    }

    <T> Callable<T> warp(Supplier<T> supplier) {
        return supplier::get;
    }

    public void call(Runnable runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }

    boolean shouldAllowExecution() {
        // 本质是Closed状态
        if (lastFailureTime == -1L) {
            return true;
        }
        // 没到达阈值
        if (errorPercentThreshold > rollingErrorPercentage()) {
            return true;
        }
        return shouldTryAfterRestTimeoutWindow()
                && changeStatus(CircuitBreakerStatus.OPEN, CircuitBreakerStatus.HALF_OPEN);
    }

    boolean changeStatus(CircuitBreakerStatus o, CircuitBreakerStatus n) {
        boolean r = status.compareAndSet(o, n);
        if (r) {
            CircuitBreakerStatusMonitor.X.report(this.circuitBreakerName, o, n);
        }
        return r;
    }

    boolean shouldTryAfterRestTimeoutWindow() {
        long lastFailureTimeSnap = lastFailureTime;
        long currentTime = System.currentTimeMillis();
        return currentTime > lastFailureTimeSnap + resetTimeout;
    }

    public void markSuccess() {
        slidingWindowMonitor.incrementSuccess();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.CLOSED)) {
            reset();
        }
    }

    public void markReject() {
        slidingWindowMonitor.incrementReject();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
    }

    public int rollingErrorPercentage() {
        MetricInfo rollingMetricInfo = slidingWindowMonitor.getRollingMetricInfo();
        long rejectCount = rollingMetricInfo.getReject();
        long failureCount = rollingMetricInfo.getFailure();
        long totalCount = rollingMetricInfo.getTotal();
        int errorPercentage = (int) ((double) (rejectCount + failureCount) / totalCount * 100);
        CircuitBreakerStatusMonitor.X.report(this.circuitBreakerName, String.format("错误百分比:%d", errorPercentage));
        return errorPercentage;
    }

    public void markFailure() {
        slidingWindowMonitor.incrementFailure();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
        if (rollingErrorPercentage() >= errorPercentThreshold &&
                changeStatus(CircuitBreakerStatus.CLOSED, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
    }
}
