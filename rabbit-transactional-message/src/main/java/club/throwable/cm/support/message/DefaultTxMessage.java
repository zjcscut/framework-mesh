package club.throwable.cm.support.message;

import lombok.Builder;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 11:15
 */
@Builder
public class DefaultTxMessage implements TxMessage {

    private String businessModule;
    private String businessKey;
    private String content;

    @Override
    public String businessModule() {
        return businessModule;
    }

    @Override
    public String businessKey() {
        return businessKey;
    }

    @Override
    public String content() {
        return content;
    }
}
