package club.throwable.cm.support.binding;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 10:02
 */
public interface Destination {

    ExchangeType exchangeType();

    String queueName();

    String exchangeName();

    String routingKey();
}
