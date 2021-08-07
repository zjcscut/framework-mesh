package cn.throwx;

import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/1 22:35
 */
@Slf4j
public class BlotApp {

    private static final int PORT = 8081;
    private static final String ADDRESS = "127.0.0.1:" + PORT;

    public static void main(String[] args) throws Exception {
        RequestMessageClientProcessor clientProcessor = new RequestMessageClientProcessor();
        RequestMessageServerProcessor serverProcessor = new RequestMessageServerProcessor();
        RpcServer server = new RpcServer(8081, true);
        server.registerUserProcessor(serverProcessor);
        server.startup();
        RpcClient client = new RpcClient();
        client.registerUserProcessor(clientProcessor);
        client.startup();
        RequestMessage request = new RequestMessage();
        request.setId(99L);
        request.setContent("hello bolt");
        ResponseMessage response = (ResponseMessage) client.invokeSync(ADDRESS, request, 2000);
        System.out.println("客户端得到响应结果:" + response);
    }
}
