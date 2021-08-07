package cn.throwx.curd;

import lombok.Data;

import java.io.Serializable;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2021/8/3 23:42
 */
@Data
public class SelectCustomerReq implements Serializable {

    private Long customerId;
}
