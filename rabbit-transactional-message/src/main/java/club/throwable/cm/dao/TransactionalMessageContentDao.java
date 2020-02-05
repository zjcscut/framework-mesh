package club.throwable.cm.dao;

import club.throwable.cm.entity.TransactionalMessageContent;

import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 11:20
 */
public interface TransactionalMessageContentDao {

    void insert(TransactionalMessageContent record);

    List<TransactionalMessageContent> queryByMessageIds(String messageIds);
}
