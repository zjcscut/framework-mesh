package cn.vlts.rocket.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/1/13 16:31
 */
@RequiredArgsConstructor
@Getter
public class RocketMessage {

    private final String destination;

    private final Message<?> message;
}
