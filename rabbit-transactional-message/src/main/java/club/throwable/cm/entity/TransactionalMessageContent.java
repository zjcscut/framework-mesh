package club.throwable.cm.entity;

import lombok.Data;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/3 11:23
 */
@Data
public class TransactionalMessageContent {

    private Long id;
    private Long messageId;
    private String content;
}
