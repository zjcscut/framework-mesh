package cn.vlts.rocket.consume;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author throwable
 * @version v1
 * @description 没有tag的消费者
 * @since 2023/1/13 16:38
 */
@Slf4j
@RocketMQMessageListener(consumerGroup = "first-topic-consumer-group-ext", topic = "first-topic")
@Component
public class OrderPaidExtConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt message) {
        log.info("first-topic-consumer-group-ext-OrderPaidExtConsumer监听到消息,内容:{}", JSON.toJSONString(message));
    }
}
