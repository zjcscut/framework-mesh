package cn.vlts.rocket.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2023/1/13 15:59
 */
@Data
public class OrderPaidEvent {

    private String orderId;

    private BigDecimal paidAmount;
}
