package cn.vlts.rabbit.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 0003 11:54
 */
@RabbitListener(id = "MultiAnnoMethodDemoConsumer", queues = "srd->srd.demo")
@Component
@Slf4j
public class MultiAnnoMethodDemoConsumer {

    @RabbitHandler
    public void firstOnMessage(Message message) {
        log.info("MultiAnnoMethodDemoConsumer.firstOnMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }

    @RabbitHandler
    public void secondOnMessage(Message message) {
        log.info("MultiAnnoMethodDemoConsumer.secondOnMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
