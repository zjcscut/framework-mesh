package cn.throwx.cb;

import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 23:28
 */
@Data
public class CircuitBreakerResource {

    private long timeout;

    private ThreadPoolExecutor executor;
}
