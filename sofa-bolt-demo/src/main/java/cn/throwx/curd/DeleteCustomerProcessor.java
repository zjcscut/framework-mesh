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
public class DeleteCustomerProcessor extends SyncUserProcessor<DeleteCustomerReq> {

    private final JdbcTemplate jdbcTemplate;

    public DeleteCustomerProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Object handleRequest(BizContext bizContext, DeleteCustomerReq req) throws Exception {
        DeleteCustomerResp resp = new DeleteCustomerResp();
        int updateCount = jdbcTemplate.update("DELETE FROM t_customer WHERE id = ?", ps -> ps.setLong(1,req.getCustomerId()));
        if (updateCount > 0){
            resp.setCode(RespCode.SUCCESS);
        }
        return resp;
    }

    @Override
    public String interest() {
        return DeleteCustomerReq.class.getName();
    }
}
