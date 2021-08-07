package cn.throwx.curd;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/3 23:46
 */
public class SelectCustomerProcessor extends SyncUserProcessor<SelectCustomerReq> {

    private final JdbcTemplate jdbcTemplate;

    public SelectCustomerProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Object handleRequest(BizContext bizContext, SelectCustomerReq req) throws Exception {
        SelectCustomerResp resp = new SelectCustomerResp();
        Customer result = jdbcTemplate.query("SELECT * FROM t_customer WHERE id = ?", ps -> ps.setLong(1, req.getCustomerId()), rs -> {
            Customer customer = null;
            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setCustomerName(rs.getString("customer_name"));
            }
            return customer;
        });
        if (Objects.nonNull(result)) {
            resp.setCustomerId(result.getId());
            resp.setCustomerName(result.getCustomerName());
            resp.setCode(RespCode.SUCCESS);
        }
        return resp;
    }

    @Override
    public String interest() {
        return SelectCustomerReq.class.getName();
    }

    @Data
    public static class Customer {

        private Long id;
        private String customerName;
    }
}
