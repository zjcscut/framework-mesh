package club.throwable.cm.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/9 14:07
 */
@Data
public class Order {

    private Long id;
    private String orderId;
    private Long userId;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private LocalDateTime editTime;
}
