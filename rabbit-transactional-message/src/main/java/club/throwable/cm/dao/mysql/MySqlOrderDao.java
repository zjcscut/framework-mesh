package club.throwable.cm.dao.mysql;

import club.throwable.cm.dao.OrderDao;
import club.throwable.cm.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/9 14:19
 */
@Repository
@RequiredArgsConstructor
public class MySqlOrderDao implements OrderDao {

    private final JdbcTemplate jdbcTemplate;

    private static final ResultSetConverter<Order> CONVERTER = r -> {
        Order order = new Order();
        order.setId(r.getLong("id"));
        order.setCreateTime(r.getTimestamp("create_time").toLocalDateTime());
        order.setEditTime(r.getTimestamp("edit_time").toLocalDateTime());
        order.setUserId(r.getLong("user_id"));
        order.setAmount(r.getBigDecimal("amount"));
        order.setOrderId(r.getString("order_id"));
        return order;
    };

    private static final ResultSetExtractor<List<Order>> MULTI = r -> {
        List<Order> list = new ArrayList<>();
        while (r.next()) {
            list.add(CONVERTER.convert(r));
        }
        return list;
    };

    private static final ResultSetExtractor<Order> SINGLE = r -> {
        if (r.next()) {
            return CONVERTER.convert(r);
        }
        return null;
    };

    @Override
    public int insertSelective(Order record) {
        List<PreparedStatementProcessor> processors = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO t_order(");
        Cursor cursor = new Cursor();
        if (null != record.getId()) {
            int idx = cursor.add();
            sql.append("id,");
            processors.add(p -> p.setLong(idx, record.getId()));
        }
        if (null != record.getOrderId()) {
            int idx = cursor.add();
            sql.append("order_id,");
            processors.add(p -> p.setString(idx, record.getOrderId()));
        }
        if (null != record.getUserId()) {
            int idx = cursor.add();
            sql.append("user_id,");
            processors.add(p -> p.setLong(idx, record.getUserId()));
        }
        if (null != record.getAmount()) {
            int idx = cursor.add();
            sql.append("amount,");
            processors.add(p -> p.setBigDecimal(idx, record.getAmount()));
        }
        if (null != record.getCreateTime()) {
            int idx = cursor.add();
            sql.append("create_time,");
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getCreateTime())));
        }
        if (null != record.getEditTime()) {
            int idx = cursor.add();
            sql.append("edit_time,");
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getEditTime())));
        }
        StringBuilder realSql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")));
        realSql.append(") VALUES (");
        int idx = cursor.idx();
        for (int i = 0; i < idx; i++) {
            if (i != idx - 1) {
                realSql.append("?,");
            } else {
                realSql.append("?");
            }
        }
        realSql.append(")");
        // 传入主键的情况
        if (null != record.getId()) {
            return jdbcTemplate.update(realSql.toString(), p -> {
                for (PreparedStatementProcessor processor : processors) {
                    processor.process(p);
                }
            });
        } else {
            // 自增主键的情况
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int count = jdbcTemplate.update(p -> {
                PreparedStatement ps = p.prepareStatement(realSql.toString(), Statement.RETURN_GENERATED_KEYS);
                for (PreparedStatementProcessor processor : processors) {
                    processor.process(ps);
                }
                return ps;
            }, keyHolder);
            record.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return count;
        }
    }

    @Override
    public int updateSelective(Order record) {
        List<PreparedStatementProcessor> processors = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE t_order SET ");
        Cursor cursor = new Cursor();
        if (null != record.getId()) {
            int idx = cursor.add();
            sql.append("id = ?,");
            processors.add(p -> p.setLong(idx, record.getId()));
        }
        if (null != record.getOrderId()) {
            int idx = cursor.add();
            sql.append("order_id = ?,");
            processors.add(p -> p.setString(idx, record.getOrderId()));
        }
        if (null != record.getUserId()) {
            int idx = cursor.add();
            sql.append("user_id = ?,");
            processors.add(p -> p.setLong(idx, record.getUserId()));
        }
        if (null != record.getAmount()) {
            int idx = cursor.add();
            sql.append("amount = ?,");
            processors.add(p -> p.setBigDecimal(idx, record.getAmount()));
        }
        if (null != record.getCreateTime()) {
            int idx = cursor.add();
            sql.append("create_time = ?,");
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getCreateTime())));
        }
        if (null != record.getEditTime()) {
            int idx = cursor.add();
            sql.append("edit_time = ?,");
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getEditTime())));
        }
        StringBuilder realSql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")));
        int idx = cursor.add();
        processors.add(p -> p.setLong(idx, record.getId()));
        realSql.append(" WHERE id = ?");
        return jdbcTemplate.update(realSql.toString(), p -> {
            for (PreparedStatementProcessor processor : processors) {
                processor.process(p);
            }
        });
    }

    @Override
    public Order selectOneByOrderId(String orderId) {
        return jdbcTemplate.query("SELECT * FROM t_order WHERE order_id = ?", p -> p.setString(1, orderId), SINGLE);
    }

    @Override
    public List<Order> selectByUserId(Long userId) {
        return jdbcTemplate.query("SELECT * FROM t_order WHERE order_id = ?", p -> p.setLong(1, userId), MULTI);
    }

    private static class Cursor {

        private int idx;

        public int add() {
            idx++;
            return idx;
        }

        public int idx() {
            return idx;
        }
    }
}
