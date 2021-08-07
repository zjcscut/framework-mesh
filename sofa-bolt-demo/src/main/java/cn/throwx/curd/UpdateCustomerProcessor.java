package cn.throwx.curd;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/3 23:46
 */
public class UpdateCustomerProcessor extends SyncUserProcessor<UpdateCustomerReq> {

    private final JdbcTemplate jdbcTemplate;

    public UpdateCustomerProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Object handleRequest(BizContext bizContext, UpdateCustomerReq req) throws Exception {
        UpdateCustomerResp resp = new UpdateCustomerResp();
        int updateCount = jdbcTemplate.update("UPDATE t_customer SET customer_name = ? WHERE id = ?", ps -> {
            ps.setString(1, req.getCustomerName());
            ps.setLong(2, req.getCustomerId());
        });
        if (updateCount > 0) {
            resp.setCode(RespCode.SUCCESS);
        }
        return resp;
    }

    @Override
    public String interest() {
        return UpdateCustomerReq.class.getName();
    }
}
