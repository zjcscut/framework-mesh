package cn.throwx.cb;

import lombok.Data;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 23:17
 */
@Data
public class CircuitBreakerResourceConf {

    private String resourceName;

    private int coreSize;

    private int queueSize;

    private long timeout;
}
