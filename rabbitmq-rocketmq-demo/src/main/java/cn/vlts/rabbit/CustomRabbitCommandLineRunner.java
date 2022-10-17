package cn.vlts.rabbit;

import cn.vlts.rabbit.config.RabbitmqToggleProperties;
import cn.vlts.rabbit.event.RabbitmqToggleRefreshEvent;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 14:20
 */
@Slf4j
@Component
public class CustomRabbitCommandLineRunner implements CommandLineRunner, ApplicationEventPublisherAware, BeanFactoryAware {

    private final ConcurrentMap<String, MessageListenerContainer> containerCache = Maps.newConcurrentMap();

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void run(String... args) throws Exception {
        // 获取声明式消费者容器
        RabbitListenerEndpointRegistry endpointRegistry = configurableListableBeanFactory.getBean(
                RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                RabbitListenerEndpointRegistry.class);
        Set<String> listenerContainerIds = endpointRegistry.getListenerContainerIds();
        for (String containerId : listenerContainerIds) {
            MessageListenerContainer messageListenerContainer = endpointRegistry.getListenerContainer(containerId);
            containerCache.putIfAbsent(containerId, messageListenerContainer);
        }
        // 获取编程式消费者容器
        Map<String, MessageListenerContainer> messageListenerContainerBeans
                = configurableListableBeanFactory.getBeansOfType(MessageListenerContainer.class);
        if (!CollectionUtils.isEmpty(messageListenerContainerBeans)) {
            messageListenerContainerBeans.forEach((beanId, bean) -> {
                if (bean instanceof AbstractMessageListenerContainer) {
                    AbstractMessageListenerContainer abstractMessageListenerContainer = (AbstractMessageListenerContainer) bean;
                    String listenerId = abstractMessageListenerContainer.getListenerId();
                    if (StringUtils.hasLength(listenerId)) {
                        containerCache.putIfAbsent(listenerId, abstractMessageListenerContainer);
                    } else {
                        containerCache.putIfAbsent(beanId, bean);
                    }
                } else {
                    containerCache.putIfAbsent(beanId, bean);
                }
            });
        }
        Set<String> listenerIds = containerCache.keySet();
        listenerIds.forEach(listenerId -> log.info("Cache message listener container => {}", listenerId));
        // 所有消费者容器Bean发现完成后才接收刷新事件
        StaticEventPublisher.attachApplicationEventPublisher(this.applicationEventPublisher);
    }

    @EventListener(classes = RabbitmqToggleRefreshEvent.class)
    public void onRabbitmqToggleRefreshEvent(RabbitmqToggleRefreshEvent event) {
        RabbitmqToggleProperties rabbitmqToggleProperties = event.getRabbitmqToggleProperties();
        List<RabbitmqToggleProperties.RabbitmqConsumer> consumers = rabbitmqToggleProperties.getConsumers();
        if (!CollectionUtils.isEmpty(consumers)) {
            consumers.forEach(consumerConf -> {
                String listenerId = consumerConf.getListenerId();
                if (StringUtils.hasLength(listenerId)) {
                    MessageListenerContainer messageListenerContainer = containerCache.get(listenerId);
                    if (Objects.nonNull(messageListenerContainer)) {
                        // running -> stop
                        if (messageListenerContainer.isRunning() && Objects.equals(Boolean.FALSE, consumerConf.getEnable())) {
                            messageListenerContainer.stop();
                            log.info("Message listener container => {} stop successfully", listenerId);
                        }
                        // modify concurrency
                        if (messageListenerContainer instanceof SimpleMessageListenerContainer) {
                            SimpleMessageListenerContainer simpleMessageListenerContainer
                                    = (SimpleMessageListenerContainer) messageListenerContainer;
                            if (Objects.nonNull(consumerConf.getConcurrentConsumers())) {
                                simpleMessageListenerContainer.setConcurrentConsumers(consumerConf.getConcurrentConsumers());
                            }
                            if (Objects.nonNull(consumerConf.getMaxConcurrentConsumers())) {
                                simpleMessageListenerContainer.setMaxConcurrentConsumers(consumerConf.getMaxConcurrentConsumers());
                            }
                        }
                        // stop -> running
                        if (!messageListenerContainer.isRunning() && Objects.equals(Boolean.TRUE, consumerConf.getEnable())) {
                            messageListenerContainer.start();
                            log.info("Message listener container => {} start successfully", listenerId);
                        }
                    }
                }
            });
        }
    }
}
