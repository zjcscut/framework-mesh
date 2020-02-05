package club.throwable.cm.dao.mysql;

import club.throwable.cm.dao.TransactionalMessageDao;
import club.throwable.cm.entity.TransactionalMessage;
import club.throwable.cm.support.message.TxMessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/4 23:31
 */
@RequiredArgsConstructor
@Repository
public class MySqlTransactionalMessageDao implements TransactionalMessageDao {

    private final JdbcTemplate jdbcTemplate;

    private static final ResultSetConverter<TransactionalMessage> CONVERTER = r -> {
        TransactionalMessage message = new TransactionalMessage();
        message.setId(r.getLong("id"));
        message.setCreateTime(r.getTimestamp("create_time").toLocalDateTime());
        message.setEditTime(r.getTimestamp("edit_time").toLocalDateTime());
        message.setCreator(r.getString("creator"));
        message.setEditor(r.getString("editor"));
        message.setDeleted(r.getInt("deleted"));
        message.setCurrentRetryTimes(r.getInt("current_retry_times"));
        message.setMaxRetryTimes(r.getInt("max_retry_times"));
        message.setQueueName(r.getString("queue_name"));
        message.setExchangeName(r.getString("exchange_name"));
        message.setExchangeType(r.getString("exchange_type"));
        message.setRoutingKey(r.getString("routing_key"));
        message.setBusinessModule(r.getString("business_module"));
        message.setBusinessKey(r.getString("business_key"));
        message.setNextScheduleTime(r.getTimestamp("next_schedule_time").toLocalDateTime());
        message.setMessageStatus(r.getInt("message_status"));
        message.setInitBackoff(r.getLong("init_backoff"));
        message.setBackoffFactor(r.getInt("backoff_factor"));
        return message;
    };

    private static final ResultSetExtractor<List<TransactionalMessage>> MULTI = r -> {
        List<TransactionalMessage> list = new ArrayList<>();
        while (r.next()) {
            list.add(CONVERTER.convert(r));
        }
        return list;
    };

    @Override
    public void insertSelective(TransactionalMessage record) {
        List<PreparedStatementProcessor> processors = new ArrayList<>();
        IndexHolder holder = new IndexHolder();
        StringBuilder sql = new StringBuilder("INSERT INTO t_transactional_message(");
        if (null != record.getCurrentRetryTimes()) {
            holder.add();
            sql.append("current_retry_times,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getCurrentRetryTimes()));
        }
        if (null != record.getMaxRetryTimes()) {
            holder.add();
            sql.append("max_retry_times,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getMaxRetryTimes()));
        }
        if (null != record.getQueueName()) {
            holder.add();
            sql.append("queue_name,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getQueueName()));
        }
        if (null != record.getExchangeName()) {
            holder.add();
            sql.append("exchange_name,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getExchangeName()));
        }
        if (null != record.getExchangeType()) {
            holder.add();
            sql.append("exchange_type,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getExchangeType()));
        }
        if (null != record.getRoutingKey()) {
            holder.add();
            sql.append("routing_key,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getRoutingKey()));
        }
        if (null != record.getBusinessModule()) {
            holder.add();
            sql.append("business_module,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getBusinessModule()));
        }
        if (null != record.getBusinessKey()) {
            holder.add();
            sql.append("business_key,");
            int idx = holder.index;
            processors.add(p -> p.setString(idx, record.getBusinessKey()));
        }
        if (null != record.getNextScheduleTime()) {
            holder.add();
            sql.append("next_schedule_time,");
            int idx = holder.index;
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getNextScheduleTime())));
        }
        if (null != record.getMessageStatus()) {
            holder.add();
            sql.append("message_status,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getMessageStatus()));
        }
        if (null != record.getInitBackoff()) {
            holder.add();
            sql.append("init_backoff,");
            int idx = holder.index;
            processors.add(p -> p.setLong(idx, record.getInitBackoff()));
        }
        if (null != record.getBackoffFactor()) {
            holder.add();
            sql.append("backoff_factor,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getBackoffFactor()));
        }
        StringBuilder realSql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")));
        realSql.append(") VALUES (");
        for (int i = 0; i < holder.index; i++) {
            if (i != holder.index - 1) {
                realSql.append("?,");
            } else {
                realSql.append("?");
            }
        }
        realSql.append(")");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(p -> {
            PreparedStatement ps = p.prepareStatement(realSql.toString(), Statement.RETURN_GENERATED_KEYS);
            for (PreparedStatementProcessor processor : processors) {
                processor.process(ps);
            }
            return ps;
        }, keyHolder);
        record.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void updateStatusSelective(TransactionalMessage record) {
        List<PreparedStatementProcessor> processors = new ArrayList<>();
        IndexHolder holder = new IndexHolder();
        StringBuilder sql = new StringBuilder("UPDATE t_transactional_message SET ");
        if (null != record.getCurrentRetryTimes()) {
            holder.add();
            sql.append("current_retry_times = ?,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getCurrentRetryTimes()));
        }
        if (null != record.getNextScheduleTime()) {
            holder.add();
            sql.append("next_schedule_time = ?,");
            int idx = holder.index;
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getNextScheduleTime())));
        }
        if (null != record.getEditTime()) {
            holder.add();
            sql.append("edit_time = ?,");
            int idx = holder.index;
            processors.add(p -> p.setTimestamp(idx, Timestamp.valueOf(record.getEditTime())));
        }
        if (null != record.getMessageStatus()) {
            holder.add();
            sql.append("message_status = ?,");
            int idx = holder.index;
            processors.add(p -> p.setInt(idx, record.getMessageStatus()));
        }
        StringBuilder realSql = new StringBuilder(sql.substring(0, sql.lastIndexOf(",")));
        holder.add();
        int idx = holder.index;
        processors.add(p -> p.setLong(idx, record.getId()));
        realSql.append(" WHERE id = ?");
        jdbcTemplate.update(realSql.toString(), p -> {
            for (PreparedStatementProcessor processor : processors) {
                processor.process(p);
            }
        });
    }

    @Override
    public List<TransactionalMessage> queryPendingCompensationRecords(LocalDateTime minScheduleTime,
                                                                      LocalDateTime maxScheduleTime,
                                                                      int limit) {
        return jdbcTemplate.query("SELECT * FROM t_transactional_message WHERE next_schedule_time >= ? " +
                        "AND next_schedule_time <= ? AND message_status <> ? AND current_retry_times < max_retry_times LIMIT ?",
                p -> {
                    p.setTimestamp(1, Timestamp.valueOf(minScheduleTime));
                    p.setTimestamp(2, Timestamp.valueOf(maxScheduleTime));
                    p.setInt(3, TxMessageStatus.SUCCESS.getStatus());
                    p.setInt(4, limit);
                },
                MULTI);
    }

    private static class IndexHolder {

        private int index;

        public void add() {
            index++;
        }
    }
}
