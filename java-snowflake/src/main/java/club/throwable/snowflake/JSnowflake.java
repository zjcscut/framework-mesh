package club.throwable.snowflake;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author thorwable
 * @description
 * @since 2020/6/15 17:08
 */
public class JSnowflake {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSnowflake.class);

    private final LongAdder genCounter = new LongAdder();

    private final LongAdder exceptionCounter = new LongAdder();

    private final long workerIdBits = 5L;
    private final long dataCenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static final int R_SEED = 100;

    private static final Random R = new Random();

    @Getter
    private long sequence = 0L;

    @Getter
    private long lastTimestamp = -1L;

    @Getter
    private final long epoch;

    @Getter
    private final long workerId;

    @Getter
    private final long dataCenterId;

    public JSnowflake(long workerId, long dataCenterId, long epoch) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.epoch = epoch;
        checkArgs(workerId, dataCenterId);
    }

    public JSnowflake(long workerId, long dataCenterId) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.epoch = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)
                .toInstant(ZoneOffset.of("+8")).toEpochMilli();
        checkArgs(workerId, dataCenterId);
    }

    private void checkArgs(long workerId, long dataCenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            exceptionCounter.increment();
            throw new IllegalArgumentException(String.format("Worker id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            exceptionCounter.increment();
            throw new IllegalArgumentException(String.format("DataCenter id can't be greater than %d or less than 0", maxDataCenterId));
        }
        LOGGER.info("Worker starting.Epoch {},timestamp left shift {}, dataCenter id bits {}, worker id bits {}, " +
                        "sequence bits {}, worker id {} dataCenter id {}",
                epoch, timestampLeftShift, dataCenterIdBits, workerIdBits, sequenceBits, workerId, dataCenterId);
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            exceptionCounter.increment();
            LOGGER.error("Clock is moving backwards.Rejecting requests until {}", lastTimestamp);
            throw new IllegalStateException(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0L) {
                timestamp = untilNextMillis(lastTimestamp);
                sequence = R.nextInt(R_SEED);
            }
        } else {
            sequence = R.nextInt(R_SEED);
        }
        lastTimestamp = timestamp;
        genCounter.increment();
        return ((timestamp - epoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long untilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public long genSum() {
        return genCounter.sum();
    }

    public long exceptionSum() {
        return exceptionCounter.sum();
    }
}
