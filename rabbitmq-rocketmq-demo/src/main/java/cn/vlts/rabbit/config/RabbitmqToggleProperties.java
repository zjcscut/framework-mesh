package cn.vlts.rabbit.config;

import cn.vlts.rabbit.StaticEventPublisher;
import cn.vlts.rabbit.event.RabbitmqToggleRefreshEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 17:54
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "rabbitmq.toggle")
public class RabbitmqToggleProperties {

    private final AtomicBoolean firstInit = new AtomicBoolean();

    private List<RabbitmqConsumer> consumers;

    @PostConstruct
    public void postConstruct() {
        if (!firstInit.compareAndSet(false, true)) {
            StaticEventPublisher.publishEvent(new RabbitmqToggleRefreshEvent(this));
            log.info("RabbitmqToggleProperties refresh, publish RabbitmqToggleRefreshEvent...");
        } else {
            log.info("RabbitmqToggleProperties first init...");
        }
    }

    @Data
    public static class RabbitmqConsumer {

        private String listenerId;

        private Integer concurrentConsumers;

        private Integer maxConcurrentConsumers;

        private Boolean enable;
    }
}
