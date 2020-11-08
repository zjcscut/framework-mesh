package cn.throwx.cb.sw;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 16:33
 */
@RequiredArgsConstructor
@Getter
public class MetricInfo {

    private final long total;

    private final long success;

    private final long failure;

    private final long reject;

    public static final MetricInfo EMPTY = new MetricInfo(0, 0, 0, 0);

    public MetricInfo merge(MetricInfo other) {
        return new MetricInfo(
                this.total + other.getTotal(),
                this.success + other.getSuccess(),
                this.failure + other.getFailure(),
                this.reject + other.getReject()
        );
    }
}
