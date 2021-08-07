package cn.throwx.curd;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/3 23:46
 */
public class CreateCustomerProcessor extends SyncUserProcessor<CreateCustomerReq> {

    private final JdbcTemplate jdbcTemplate;

    public CreateCustomerProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Object handleRequest(BizContext bizContext, CreateCustomerReq req) throws Exception {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into t_customer(customer_name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, req.getCustomerName());
            return ps;
        }, keyHolder);
        CreateCustomerResp resp = new CreateCustomerResp();
        resp.setCustomerId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        resp.setCode(RespCode.SUCCESS);
        return resp;
    }

    @Override
    public String interest() {
        return CreateCustomerReq.class.getName();
    }
}
