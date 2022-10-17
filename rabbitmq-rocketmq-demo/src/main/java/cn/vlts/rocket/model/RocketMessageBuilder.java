package cn.vlts.rocket.model;

import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/1/13 16:26
 */
public class RocketMessageBuilder<T> {

    private String topic;

    private String tags;

    private String keys;

    private final T payload;

    public RocketMessageBuilder(T payload) {
        this.payload = payload;
    }

    public static <T> RocketMessageBuilder<T> newBuilder(T payload) {
        return new RocketMessageBuilder<>(payload);
    }

    public RocketMessageBuilder<T> topic(String topic) {
        this.topic = topic;
        return this;
    }

    public RocketMessageBuilder<T> tags(String tags) {
        this.tags = tags;
        return this;
    }

    public RocketMessageBuilder<T> keys(String keys) {
        this.keys = keys;
        return this;
    }


    public RocketMessage build() {
        Assert.notNull(this.topic, "topic must not be null");
        Assert.notNull(this.payload, "payload must not be null");
        MessageBuilder<T> messageBuilder = MessageBuilder.withPayload(this.payload);
        if (Objects.nonNull(keys)) {
            messageBuilder.setHeader(RocketMQHeaders.KEYS, keys);
        }
        return new RocketMessage(genDestination(), messageBuilder.build());
    }

    private String genDestination() {
        if (Objects.isNull(tags)) {
            return topic;
        }
        return topic + ":" + tags;
    }
}
