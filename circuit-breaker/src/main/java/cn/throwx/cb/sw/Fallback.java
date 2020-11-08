package cn.throwx.cb.sw;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2020/10/25 17:29
 */
public interface Fallback<T> {

    Fallback<?> F = () -> null;

    T fallback();
}
