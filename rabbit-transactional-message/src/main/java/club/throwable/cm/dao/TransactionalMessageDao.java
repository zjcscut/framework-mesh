package club.throwable.cm.dao;

import club.throwable.cm.entity.TransactionalMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 11:20
 */
public interface TransactionalMessageDao {

    void insertSelective(TransactionalMessage record);

    void updateStatusSelective(TransactionalMessage record);

    List<TransactionalMessage> queryPendingCompensationRecords(LocalDateTime minScheduleTime,
                                                               LocalDateTime maxScheduleTime,
                                                               int limit);
}
