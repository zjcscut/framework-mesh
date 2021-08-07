package cn.throwx;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/1 22:27
 */
@Slf4j
public class RequestMessageClientProcessor extends SyncUserProcessor<RequestMessage> {

    @Override
    public Object handleRequest(BizContext bizContext, RequestMessage requestMessage) throws Exception {
        ResponseMessage message = new ResponseMessage();
        message.setContent(requestMessage.getContent());
        message.setId(requestMessage.getId());
        message.setStatus(10088L);
        return message;
    }

    @Override
    public String interest() {
        return RequestMessage.class.getName();
    }
}
