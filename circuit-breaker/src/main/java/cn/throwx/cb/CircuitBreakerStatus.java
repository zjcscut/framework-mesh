package cn.throwx.cb;

/**
 * @author throwable
 * @version v1
 * @description 断路器状态
 * @since 2020/10/24 19:08
 */
public enum CircuitBreakerStatus {

    /**
     * 关闭
     */
    CLOSED,

    /**
     * 开启
     */
    OPEN,

    /**
     * 半开启
     */
    HALF_OPEN
}
