package club.throwable.cm.support.message;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 10:50
 */
public interface TxMessage {

    String businessModule();

    String businessKey();

    String content();
}
