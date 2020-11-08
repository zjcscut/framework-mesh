package cn.throwx.cb;

import lombok.Getter;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 19:10
 */
@Getter
public class ResourceCircuitBreaker {

    private final long failureThreshold;
    private final long resetTimeout;
    private LongAdder failureCounter;
    private LongAdder callCounter;
    private AtomicReference<CircuitBreakerStatus> status;
    private final ThreadPoolExecutor executor;
    private final Object fallback = null;
    private final String circuitBreakerName;

    /**
     * 最后一次调用失败的时间戳
     */
    private long lastFailureTime;

    /**
     * 执行超时上限,单位毫秒
     */
    private final long executionTimeout;

    public ResourceCircuitBreaker(String resourceName, long failureThreshold, long resetTimeout) {
        CircuitBreakerResource resource = CircuitBreakerResourceManager.X.get(resourceName);
        this.circuitBreakerName = "ResourceCircuitBreaker-" + resourceName;
        this.executor = resource.getExecutor();
        this.executionTimeout = resource.getTimeout();
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        reset();
    }

    public void reset() {
        CircuitBreakerStatusMonitor.X.reset(this.circuitBreakerName);
        this.callCounter = new LongAdder();
        this.failureCounter = new LongAdder();
        this.status = new AtomicReference<>(CircuitBreakerStatus.CLOSED);
        this.lastFailureTime = -1L;
    }

    @SuppressWarnings("unchecked")
    public <T> T call(Supplier<T> supplier) {
        try {
            if (shouldAllowExecution()) {
                Future<T> future = this.executor.submit(warp(supplier));
                T result = future.get(executionTimeout, TimeUnit.MILLISECONDS);
                markSuccess();
                return result;
            }
        } catch (Exception e) {
            markNoneSuccess();
        } finally {
            this.callCounter.increment();
        }
        return (T) fallback;
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
        if (failureThreshold > failureCounter.sum()) {
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
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.CLOSED)) {
            reset();
        }
    }

    public void markNoneSuccess() {
        this.failureCounter.increment();
        if (changeStatus(CircuitBreakerStatus.HALF_OPEN, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
        if (this.failureCounter.sum() >= failureThreshold &&
                changeStatus(CircuitBreakerStatus.CLOSED, CircuitBreakerStatus.OPEN)) {
            this.lastFailureTime = System.currentTimeMillis();
        }
    }
}
