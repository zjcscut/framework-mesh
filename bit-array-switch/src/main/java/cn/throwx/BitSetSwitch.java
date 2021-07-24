package cn.throwx;

import java.util.BitSet;
import java.util.stream.Stream;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/7/23 0:58
 */
public enum BitSetSwitch implements Switch {

    /**
     * 单例
     */
    X;

    BitSetSwitch() {
        init();
    }

    private final BitSet switches = new BitSet();

    @Override
    public void turnOn(SwitchDef switchDef) {
        switches.set(switchDef.getIndex(), SwitchConst.ON);
    }

    @Override
    public void turnOff(SwitchDef switchDef) {
        switches.clear(switchDef.getIndex());
    }

    @Override
    public boolean status(SwitchDef switchDef) {
        return switches.get(switchDef.getIndex());
    }

    private void init() {
        Stream.of(SwitchDef.values()).forEach(item -> switches.set(item.getIndex(), item.isDefaultStatus()));
    }

    public static void main(String[] args) {
        Switch s = BitSetSwitch.X;
        s.turnOn(SwitchDef.ENABLE_HTTPS);
        s.turnOff(SwitchDef.ENABLE_ASYNC);
        System.out.printf("开关[%s],状态:%s%n", SwitchDef.ENABLE_HTTPS, s.status(SwitchDef.ENABLE_HTTPS));
        System.out.printf("开关[%s],状态:%s%n", SwitchDef.ENABLE_ASYNC, s.status(SwitchDef.ENABLE_ASYNC));
    }
}
