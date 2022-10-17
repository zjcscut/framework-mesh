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
 * @since 2023/2/3 11:54
 */
@Slf4j
@RabbitListener(id = "SingleAnnoMethodDemoConsumer", queues = "srd->srd.demo")
@Component
public class SingleAnnoMethodDemoConsumer {

    @RabbitHandler
    public void onMessage(Message message) {
        log.info("SingleAnnoMethodDemoConsumer.onMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
