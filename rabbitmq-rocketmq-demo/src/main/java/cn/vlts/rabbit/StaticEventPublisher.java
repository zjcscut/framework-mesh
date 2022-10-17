package cn.vlts.rabbit;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 18:31
 */
public class StaticEventPublisher {

    private static ApplicationEventPublisher PUBLISHER = null;

    public static void publishEvent(ApplicationEvent applicationEvent) {
        if (Objects.nonNull(PUBLISHER)) {
            PUBLISHER.publishEvent(applicationEvent);
        }
    }

    public static void attachApplicationEventPublisher(ApplicationEventPublisher publisher) {
        PUBLISHER = publisher;
    }
}
