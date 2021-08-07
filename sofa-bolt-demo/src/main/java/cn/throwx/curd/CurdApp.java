package cn.throwx.curd;

import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.RpcServer;
import com.mysql.cj.jdbc.Driver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/3 23:51
 */
public class CurdApp {

    private static final int PORT = 8081;
    private static final String ADDRESS = "127.0.0.1:" + PORT;

    public static void main(String[] args) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        config.setDriverClassName(Driver.class.getName());
        config.setUsername("root");
        config.setPassword("root");
        HikariDataSource dataSource = new HikariDataSource(config);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        CreateCustomerProcessor createCustomerProcessor = new CreateCustomerProcessor(jdbcTemplate);
        UpdateCustomerProcessor updateCustomerProcessor = new UpdateCustomerProcessor(jdbcTemplate);
        DeleteCustomerProcessor deleteCustomerProcessor = new DeleteCustomerProcessor(jdbcTemplate);
        SelectCustomerProcessor selectCustomerProcessor = new SelectCustomerProcessor(jdbcTemplate);
        RpcServer server = new RpcServer(PORT, true);
        server.registerUserProcessor(createCustomerProcessor);
        server.registerUserProcessor(updateCustomerProcessor);
        server.registerUserProcessor(deleteCustomerProcessor);
        server.registerUserProcessor(selectCustomerProcessor);
        server.startup();
        RpcClient client = new RpcClient();
        client.startup();
        CreateCustomerReq createCustomerReq = new CreateCustomerReq();
        createCustomerReq.setCustomerName("throwable.club");
        CreateCustomerResp createCustomerResp = (CreateCustomerResp)
                client.invokeSync(ADDRESS, createCustomerReq, 5000);
        System.out.println("创建用户[throwable.club]结果:" + createCustomerResp);
        SelectCustomerReq selectCustomerReq = new SelectCustomerReq();
        selectCustomerReq.setCustomerId(createCustomerResp.getCustomerId());
        SelectCustomerResp selectCustomerResp = (SelectCustomerResp)
                client.invokeSync(ADDRESS, selectCustomerReq, 5000);
        System.out.println(String.format("查询用户[id=%d]结果:%s", selectCustomerReq.getCustomerId(),
                selectCustomerResp));
        UpdateCustomerReq updateCustomerReq = new UpdateCustomerReq();
        updateCustomerReq.setCustomerId(selectCustomerReq.getCustomerId());
        updateCustomerReq.setCustomerName("throwx.cn");
        UpdateCustomerResp updateCustomerResp = (UpdateCustomerResp)
                client.invokeSync(ADDRESS, updateCustomerReq, 5000);
        System.out.println(String.format("更新用户[id=%d]结果:%s", updateCustomerReq.getCustomerId(),
                updateCustomerResp));
        selectCustomerReq.setCustomerId(updateCustomerReq.getCustomerId());
        selectCustomerResp = (SelectCustomerResp)
                client.invokeSync(ADDRESS, selectCustomerReq, 5000);
        System.out.println(String.format("查询更新后的用户[id=%d]结果:%s", selectCustomerReq.getCustomerId(),
                selectCustomerResp));
        DeleteCustomerReq deleteCustomerReq = new DeleteCustomerReq();
        deleteCustomerReq.setCustomerId(selectCustomerResp.getCustomerId());
        DeleteCustomerResp deleteCustomerResp = (DeleteCustomerResp)
                client.invokeSync(ADDRESS, deleteCustomerReq, 5000);
        System.out.println(String.format("删除用户[id=%d]结果:%s", deleteCustomerReq.getCustomerId(),
                deleteCustomerResp));
    }
}
