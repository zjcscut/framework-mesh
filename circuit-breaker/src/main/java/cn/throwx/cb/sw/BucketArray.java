package cn.throwx.cb.sw;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 12:21
 */
public class BucketArray {

    private final AtomicReferenceArray<Bucket> buckets;

    /**
     * 数组长度
     */
    private final int len;

    /**
     * 当前数组中的元素个数
     */
    @Getter
    private final int size;

    /**
     * 预设的桶数量 - 不会更变
     */
    private final int bucketNumber;
    private final int tailPointer;
    private final int headPointer;

    BucketArray(AtomicReferenceArray<Bucket> buckets, int head, int tail, int bucketNumber) {
        this.headPointer = head;
        this.tailPointer = tail;
        this.len = buckets.length();
        // 首次初始化
        if (0 == head && 0 == tail) {
            this.size = 0;
        } else {
            this.size = (tail + len - head) % len;
        }
        this.buckets = buckets;
        this.bucketNumber = bucketNumber;
    }

    int idx(int index) {
        return (index + headPointer) % len;
    }

    public BucketArray addBucket(Bucket bucket) {
        this.buckets.set(tailPointer, bucket);
        if (size == bucketNumber) {
            return new BucketArray(buckets, (headPointer + 1) % len, (tailPointer + 1) % len, bucketNumber);
        } else {
            return new BucketArray(buckets, headPointer, (tailPointer + 1) % len, bucketNumber);
        }
    }

    public Bucket tail() {
        if (0 == size) {
            return null;
        }
        return buckets.get(idx(size - 1));
    }

    public Bucket[] toArray() {
        List<Bucket> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(buckets.get(idx(i)));
        }
        return array.toArray(new Bucket[0]);
    }

    public BucketArray clear() {
        return new BucketArray(new AtomicReferenceArray<>(len), 0, 0, bucketNumber);
    }
}
