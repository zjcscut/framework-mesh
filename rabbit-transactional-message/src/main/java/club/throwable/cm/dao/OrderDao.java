package club.throwable.cm.dao;

import club.throwable.cm.entity.Order;

import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2020/2/9 14:16
 */
public interface OrderDao {

    int insertSelective(Order record);

    int updateSelective(Order record);

    Order selectOneByOrderId(String orderId);

    List<Order> selectByUserId(Long userId);
}
