package cn.vlts.rocket.consume;

import cn.vlts.rocket.model.OrderPaidEvent;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
@RocketMQMessageListener(consumerGroup = "first-topic-consumer-group-sec", topic = "first-topic", selectorExpression = "orderPaid")
@Component
public class OrderPaidWithTagConsumer implements RocketMQListener<OrderPaidEvent> {

    @Override
    public void onMessage(OrderPaidEvent message) {
        log.info("first-topic-consumer-group-sec-OrderPaidWithTagConsumer监听到消息,内容:{}", JSON.toJSONString(message));
    }
}
