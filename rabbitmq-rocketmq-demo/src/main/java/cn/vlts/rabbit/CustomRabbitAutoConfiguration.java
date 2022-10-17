package cn.vlts.rabbit;

import cn.vlts.rabbit.config.RabbitmqToggleProperties;
import cn.vlts.rabbit.consume.CustomMethodDemoConsumer;
import cn.vlts.rocket.config.FeatureToggleProperties;
import cn.vlts.rocket.config.NacosExtensionProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/2/3 11:38
 */
@Configuration
@EnableConfigurationProperties(value = {RabbitmqToggleProperties.class})
public class CustomRabbitAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        AmqpAdmin rabbitAdmin = configurableListableBeanFactory.getBean(AmqpAdmin.class);
        Stream.of(RabbitQueue.values()).forEach(qd -> {
            Queue queue = new Queue(qd.getQueueName());
            rabbitAdmin.declareQueue(queue);
            CustomExchange exchange = new CustomExchange(qd.getExchangeName(), qd.getExchangeType());
            rabbitAdmin.declareExchange(exchange);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(qd.getRoutingKey()).noargs();
            rabbitAdmin.declareBinding(binding);
        });
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerDemoConsumerContainer(
            ConnectionFactory connectionFactory,
            @Qualifier("messageListenerDemoConsumer") MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setListenerId("MessageListenerDemoConsumer");
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setQueueNames("srd->srd.demo");
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setPrefetchCount(10);
        container.setMessageListener(messageListener);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer customMethodDemoConsumerContainer(
            ConnectionFactory connectionFactory,
            CustomMethodDemoConsumer customMethodDemoConsumer) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setListenerId("CustomMethodDemoConsumer");
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setQueueNames("srd->srd.demo");
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setPrefetchCount(10);
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(customMethodDemoConsumer);
        messageListenerAdapter.setDefaultListenerMethod("customOnMessage");
        container.setMessageListener(messageListenerAdapter);
        return container;
    }
}
