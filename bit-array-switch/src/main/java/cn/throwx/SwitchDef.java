package cn.throwx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author throwable
 * @version v1
 * @description 开关定义
 * @since 2021/7/24 23:28
 */
@RequiredArgsConstructor
@Getter
public enum SwitchDef {

    /**
     * 启用HTTPS
     */
    ENABLE_HTTPS(0, SwitchConst.ON, "启用HTTPS"),

    /**
     * 启用异步
     */
    ENABLE_ASYNC(1, SwitchConst.OFF, "启用异步"),

    ;

    /**
     * 下标
     */
    private final int index;

    /**
     * 默认状态
     */
    private final boolean defaultStatus;

    /**
     * 描述
     */
    private final String description;

    @Override
    public String toString() {
        return String.format("SwitchDef(name=%s,description=%s)", name(), description);
    }
}
