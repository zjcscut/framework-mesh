package cn.vlts.rabbit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 11:41
 */
@RequiredArgsConstructor
@Getter
public enum RabbitQueue {

    /**
     * demo
     */
    DEMO("srd->srd.demo", "srd->srd.demo.exchange", "fanout", "srd->srd.demo.key"),

    /**
     * sec
     */
    SEC("srd->srd.sec", "srd->srd.sec.exchange", "fanout", "srd->srd.sec.key"),

    ;

    private final String queueName;
    private final String exchangeName;
    private final String exchangeType;
    private final String routingKey;
}
