package cn.throwx;

/**
 * @author throwable
 * @version v1
 * @description 开关接口
 * @since 2021/7/23 0:56
 */
public interface Switch {

    /**
     * 启用
     *
     * @param switchDef switchDef
     */
    void turnOn(SwitchDef switchDef);

    /**
     * 关闭
     *
     * @param switchDef switchDef
     */
    void turnOff(SwitchDef switchDef);

    /**
     * 判断状态
     *
     * @param switchDef switchDef
     * @return boolean
     */
    boolean status(SwitchDef switchDef);
}
