package cn.throwx.cb;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/24 22:21
 */
public enum CircuitBreakerStatusMonitor {

    /**
     * 单例
     */
    X;

    public void report(String name, CircuitBreakerStatus o, CircuitBreakerStatus n) {
        System.out.println(String.format("断路器[%s]状态变更,[%s]->[%s]", name, o, n));
    }

    public void reset(String name) {
        System.out.println(String.format("断路器[%s]重置", name));
    }

    public void report(String name, String text) {
        System.out.println(String.format("断路器[%s]-%s", name, text));
    }
}
