package cn.vlts.rabbit.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 0003 11:54
 */
@Component
@Slf4j
public class MultiAnnoInstanceDemoConsumer {

    @RabbitListener(id = "MultiAnnoInstanceDemoConsumer-firstOnInstanceMessage", queues = "srd->srd.demo")
    public void firstOnInstanceMessage(Message message) {
        log.info("MultiAnnoInstanceDemoConsumer.firstOnInstanceMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }

    @RabbitListener(id = "MultiAnnoInstanceDemoConsumer-secondOnInstanceMessage", queues = "srd->srd.sec")
    public void secondOnInstanceMessage(Message message) {
        log.info("MultiAnnoInstanceDemoConsumer.secondOnInstanceMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
