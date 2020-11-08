package cn.throwx.cb.sw;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 12:47
 */
public class BucketCircular implements Iterable<Bucket> {

    private final AtomicReference<BucketArray> bucketArray;

    public BucketCircular(int bucketNumber) {
        AtomicReferenceArray<Bucket> buckets = new AtomicReferenceArray<>(bucketNumber + 1);
        this.bucketArray = new AtomicReference<>(new BucketArray(buckets, 0, 0, bucketNumber));
    }

    public Bucket getTail() {
        return this.bucketArray.get().tail();
    }

    /**
     * 在环形队列尾部添加一个桶
     */
    public void addTail(Bucket bucket) {
        BucketArray bucketArray = this.bucketArray.get();
        BucketArray newBucketArray = bucketArray.addBucket(bucket);
        // 这个方法会在锁中执行,理论上不会CAS失败
        this.bucketArray.compareAndSet(bucketArray, newBucketArray);
    }

    public Bucket[] toArray() {
        return this.bucketArray.get().toArray();
    }

    public int size() {
        return this.bucketArray.get().getSize();
    }

    @Override
    public Iterator<Bucket> iterator() {
        return Collections.unmodifiableList(Arrays.asList(toArray())).iterator();
    }

    public void clear() {
        while (true) {
            BucketArray bucketArray = this.bucketArray.get();
            BucketArray clear = bucketArray.clear();
            if (this.bucketArray.compareAndSet(bucketArray, clear)) {
                return;
            }
        }
    }
}
