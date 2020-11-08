package cn.throwx.cb.sw;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author throwable
 * @version v1
 * @description 累计计算器
 * @since 2020/10/25 16:31
 */
public class BucketCumulativeCalculator {

    private LongAdder total = new LongAdder();
    private LongAdder success = new LongAdder();
    private LongAdder failure = new LongAdder();
    private LongAdder reject = new LongAdder();

    public void addBucket(Bucket lb) {
        total.add(lb.totalCount());
        success.add(lb.successCount());
        failure.add(lb.failureCount());
        reject.add(lb.rejectCount());
    }

    public MetricInfo sum() {
        return new MetricInfo(
                total.sum(),
                success.sum(),
                failure.sum(),
                reject.sum()
        );
    }

    public void reset() {
        total = new LongAdder();
        success = new LongAdder();
        failure = new LongAdder();
        reject = new LongAdder();
    }
}
