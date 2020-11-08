package cn.throwx.cb.sw;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author throwable
 * @version v1
 * @description 下面的几个参数为了简单起见暂时固定
 * @since 2020/10/25 15:45
 */
public class SlidingWindowMonitor {

    /**
     * 窗口长度 - 10秒
     */
    private final int windowDuration = 10000;

    /**
     * 桶的大小 - 时间单位为1秒
     */
    private final int bucketSizeInTimeUint = 1000;

    /**
     * 桶的数量 - 必须满足windowDuration % bucketSizeInTimeUint = 0
     */
    private final int bucketNumber = windowDuration / bucketSizeInTimeUint;

    private final BucketCircular bucketCircular;

    /**
     * 用于创建桶的时候进行锁定
     */
    private final ReentrantLock lock;

    /**
     * 累计计数器
     */
    private final BucketCumulativeCalculator calculator = new BucketCumulativeCalculator();

    public SlidingWindowMonitor() {
        this.bucketCircular = new BucketCircular(bucketNumber);
        this.lock = new ReentrantLock();
    }

    void reset() {
        Bucket tailBucket = bucketCircular.getTail();
        if (null != tailBucket) {
            calculator.addBucket(tailBucket);
        }
        bucketCircular.clear();
    }

    /**
     * 累计统计
     */
    public MetricInfo getCumulativeMetricInfo() {
        return getCurrentMetricInfo().merge(calculator.sum());
    }

    /**
     * 当前统计
     */
    public MetricInfo getCurrentMetricInfo() {
        Bucket currentBucket = getCurrentBucket();
        if (null == currentBucket) {
            return MetricInfo.EMPTY;
        }
        return currentBucket.metricInfo();
    }

    /**
     * 滚动统计 - 这个就是断路器计算错误请求百分比的来源数据
     */
    public MetricInfo getRollingMetricInfo() {
        Bucket currentBucket = getCurrentBucket();
        if (null == currentBucket) {
            return MetricInfo.EMPTY;
        }
        MetricInfo info = new MetricInfo(0, 0, 0, 0);
        for (Bucket bucket : this.bucketCircular) {
            info = info.merge(bucket.metricInfo());
        }
        return info;
    }

    /**
     * 这个方法是核心 - 用于获取当前系统时间对应的Bucket
     */
    Bucket getCurrentBucket() {
        long time = System.currentTimeMillis();
        Bucket tailBucket = bucketCircular.getTail();
        // 队尾的桶还在当前的时间所在的桶区间内则直接使用此桶
        if (null != tailBucket && time < tailBucket.getWindowStartTimestamp() + bucketSizeInTimeUint) {
            return tailBucket;
        }
        if (lock.tryLock()) {
            try {
                // 循环队列为空
                if (null == bucketCircular.getTail()) {
                    Bucket newBucket = new Bucket(time);
                    bucketCircular.addTail(newBucket);
                    return newBucket;
                } else {
                    // 需要创建足够多的桶以追上当前的时间
                    for (int i = 0; i < bucketNumber; i++) {
                        tailBucket = bucketCircular.getTail();
                        // 最新的一个桶已经追上了当前时间
                        if (time < tailBucket.getWindowStartTimestamp() + bucketSizeInTimeUint) {
                            return tailBucket;
                        }
                        // 当前时间已经到了下一个窗口
                        else if (time > tailBucket.getWindowStartTimestamp() + bucketSizeInTimeUint + windowDuration) {
                            reset();
                            return getCurrentBucket();
                        }
                        // 这种情况是当前最新时间比窗口超前,要填补过去的桶
                        else {
                            bucketCircular.addTail(new Bucket(tailBucket.getWindowStartTimestamp() + bucketSizeInTimeUint));
                            calculator.addBucket(tailBucket);
                        }
                    }
                    return bucketCircular.getTail();
                }
            } finally {
                lock.unlock();
            }
        } else {
            // 获取锁失败说明多线程并发创建桶,再获取一次不空则为另一个获取锁成功的线程创建的最新的桶,否则需要进行线程等待和递归获取
            tailBucket = bucketCircular.getTail();
            if (null != tailBucket) {
                return tailBucket;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException ignore) {
            }
            // 递归
            return getCurrentBucket();
        }
    }

    public void incrementTotal() {
        getCurrentBucket().increaseTotal();
    }

    public void incrementSuccess() {
        getCurrentBucket().increaseSuccess();
    }

    public void incrementFailure() {
        getCurrentBucket().increaseFailure();
    }

    public void incrementReject() {
        getCurrentBucket().increaseReject();
    }
}
