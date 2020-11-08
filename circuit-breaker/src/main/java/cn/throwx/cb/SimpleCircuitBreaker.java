package cn.throwx.cb;

import cn.throwx.cb.CircuitBreakerStatus;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 19:08
 */
@Getter
public class SimpleCircuitBreaker {

    private final long failureThreshold;
    private final LongAdder failureCounter;
    private final LongAdder callCounter;
    private final AtomicReference<CircuitBreakerStatus> status;

    public SimpleCircuitBreaker(long failureThreshold) {
        this.failureThreshold = failureThreshold;
        this.callCounter = new LongAdder();
        this.failureCounter = new LongAdder();
        this.status = new AtomicReference<>(CircuitBreakerStatus.CLOSED);
    }

    private final Object fallback = null;

    @SuppressWarnings("unchecked")
    public <T> T call(Supplier<T> supplier) {
        try {
            if (CircuitBreakerStatus.CLOSED == this.status.get()) {
                return supplier.get();
            }
        } catch (Exception e) {
            this.failureCounter.increment();
            tryChangingStatus();
        } finally {
            this.callCounter.increment();
        }
        return (T) fallback;
    }

    private void tryChangingStatus() {
        if (this.failureThreshold <= this.failureCounter.sum()) {
            boolean b = this.status.compareAndSet(CircuitBreakerStatus.CLOSED, CircuitBreakerStatus.OPEN);
            if (b) {
                System.out.println(String.format("SimpleCircuitBreaker状态转换,[%s]->[%s]", CircuitBreakerStatus.CLOSED,
                        CircuitBreakerStatus.OPEN));
            }
        }
    }

    public void call(Runnable runnable) {
        call(() -> {
            runnable.run();
            return null;
        });
    }
}
