package cn.vlts.ulid.benchmark;

import cn.vlts.ulid.ULID;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author throwable
 * @version v1
 * @description benchmark
 * @since 2022/10/16 21:50
 */
@Fork(1)
@Threads(10)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkRunner {

    private static ULID.MonotonicULIDSpi SPI;

    @Setup
    public void setup() {
        SPI = ULID.monotonicUlid();
    }


    @Benchmark
    public UUID createUUID() {
        return UUID.randomUUID();
    }

    @Benchmark
    public String createUUIDToString() {
        return UUID.randomUUID().toString();
    }

    @Benchmark
    public ULID createULID() {
        return ULID.ulid();
    }

    @Benchmark
    public String createULIDToString() {
        return ULID.ulid().toString();
    }

    @Benchmark
    public ULID createMonotonicULID() {
        return SPI.next();
    }

    @Benchmark
    public String createMonotonicULIDToString() {
        return SPI.next().toString();
    }

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder().build()).run();
    }
}
