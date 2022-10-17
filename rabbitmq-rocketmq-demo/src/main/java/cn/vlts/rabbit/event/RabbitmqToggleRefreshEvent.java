package cn.vlts.rabbit.event;

import cn.vlts.rabbit.config.RabbitmqToggleProperties;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 18:06
 */
@Getter
public class RabbitmqToggleRefreshEvent extends ApplicationEvent {

    private final RabbitmqToggleProperties rabbitmqToggleProperties;

    public RabbitmqToggleRefreshEvent(RabbitmqToggleProperties rabbitmqToggleProperties) {
        super("RabbitmqToggleRefreshEvent");
        this.rabbitmqToggleProperties = rabbitmqToggleProperties;
    }
}
