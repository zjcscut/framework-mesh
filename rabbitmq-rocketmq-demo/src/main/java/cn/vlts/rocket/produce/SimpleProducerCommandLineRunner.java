package cn.vlts.rocket.produce;

import cn.vlts.rocket.config.FeatureToggleProperties;
import cn.vlts.rocket.model.OrderPaidEvent;
import cn.vlts.rocket.model.RocketMessage;
import cn.vlts.rocket.model.RocketMessageBuilder;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author throwable
 * @version v1
 * @description simple producer
 * @since 2023/1/13 15:49
 */
@Slf4j
@Component
public class SimpleProducerCommandLineRunner implements CommandLineRunner {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private FeatureToggleProperties featureToggleProperties;

    @Value("${rocketmq.custom.topic}")
    private String topic;

    @Value("${rocketmq.custom.orderPaidTag}")
    private String tags;

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int count = 0;
            for (; ; ) {
                if (count > 10) {
                    break;
                }
                count++;
                Map<String, Boolean> rocketmqProducers = featureToggleProperties.getRocketmqProducers();
                log.info("第{}次执行,rocketmqProducers => {}", count, JSON.toJSONString(rocketmqProducers));
                Boolean enableTopic = rocketmqProducers.get(topic);
                if (Objects.equals(Boolean.TRUE, enableTopic)) {
                    OrderPaidEvent orderPaidEvent = createOrderPaidEvent();
                    RocketMessage rocketMessage = RocketMessageBuilder.newBuilder(orderPaidEvent).topic(topic).tags(tags).keys(orderPaidEvent.getOrderId()).build();
                    SendResult sendResult = rocketMQTemplate.syncSend(rocketMessage.getDestination(), rocketMessage.getMessage());
                    log.info("Sync send invoke,result => {}", JSON.toJSONString(sendResult));
                    orderPaidEvent = createOrderPaidEvent();
                    rocketMessage = RocketMessageBuilder.newBuilder(orderPaidEvent).topic(topic).tags(tags).keys(orderPaidEvent.getOrderId()).build();
                    rocketMQTemplate.asyncSend(rocketMessage.getDestination(), rocketMessage.getMessage(), new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sr) {
                            log.info("Async send invoke,result => {}", JSON.toJSONString(sr));
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.error("Async send error", throwable);
                        }
                    });
                }
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignore) {

                }
            }
        });
    }

    private OrderPaidEvent createOrderPaidEvent() {
        Random random = new Random();
        OrderPaidEvent event = new OrderPaidEvent();
        event.setOrderId(UUID.randomUUID().toString());
        event.setPaidAmount(BigDecimal.valueOf(random.nextInt(10) + 1));
        return event;
    }
}
