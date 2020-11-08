package cn.throwx.cb.sw;

import lombok.Getter;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 11:52
 */
public class Bucket {

    @Getter
    private final long windowStartTimestamp;
    private final LongAdder total;
    private final LongAdder success;
    private final LongAdder failure;
    private final LongAdder reject;

    public Bucket(long windowStartTimestamp) {
        this.windowStartTimestamp = windowStartTimestamp;
        this.total = new LongAdder();
        this.success = new LongAdder();
        this.reject = new LongAdder();
        this.failure = new LongAdder();
    }

    public void increaseTotal() {
        this.total.increment();
    }

    public void increaseSuccess() {
        this.success.increment();
    }

    public void increaseFailure() {
        this.failure.increment();
    }

    public void increaseReject() {
        this.reject.increment();
    }

    public long totalCount() {
        return this.total.sum();
    }

    public long successCount() {
        return this.success.sum();
    }

    public long failureCount() {
        return this.failure.sum();
    }

    public long rejectCount() {
        return this.reject.sum();
    }

    public void reset() {
        this.total.reset();
        this.success.reset();
        this.failure.reset();
        this.reject.reset();
    }

    public MetricInfo metricInfo() {
        return new MetricInfo(
                totalCount(),
                successCount(),
                failureCount(),
                rejectCount()
        );
    }

    @Override
    public String toString() {
        return String.format("Bucket[wt=%d,t=%d,s=%d,f=%d,r=%d]",
                windowStartTimestamp,
                totalCount(),
                successCount(),
                failureCount(),
                rejectCount()
        );
    }
}
