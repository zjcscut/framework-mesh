package cn.vlts.rabbit.consume;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 14:15
 */
@Component
@Slf4j
public class CustomMethodDemoConsumer {

    public void customOnMessage(Message message) {
        log.info("CustomMethodDemoConsumer.customOnMessage => {}", new String(message.getBody(), StandardCharsets.UTF_8));
    }
}
