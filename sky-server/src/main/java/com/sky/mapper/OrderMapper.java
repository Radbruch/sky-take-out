package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderDetailForAdminVO;
import com.sky.vo.OrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insertOrder(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber} ORDER BY order_time DESC")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("SELECT * FROM orders WHERE id = #{id} ORDER BY order_time DESC")
    Orders getOrderById(Long id);

    /**
     * 通过id得到order+orderdetails的所有信息
     * @param userId
     * @return
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY order_time DESC")
    Page<OrderDetailVO> getOrdersByUserId(Long userId);

    /**
     * 取消订单
     * @param order
     */
    @Update("UPDATE orders SET status = #{status}, cancel_time = #{cancelTime}")
    void cancel(Orders order);

    /**
     * 条件分页查找
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderDetailForAdminVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 得到订单里的的所有菜名
     * @param id
     * @return
     */
    @Select("SELECT name FROM order_detail WHERE order_id = #{id}")
    List<String> getOrdersNameById(Long id);

    /**
     * 完成订单
     * @param id
     */
    @Update("UPDATE orders SET status = 5 WHERE id = #{id}")
    void complete(Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Update("UPDATE orders SET status = 6, rejection_reason = #{rejectionReason} WHERE id = #{id}")
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    @Update("UPDATE orders SET status = 4 WHERE id = #{id}")
    void delivery(Long id);

    /**
     * 根据订单状态和下单时间查询订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("SELECT * FROM orders WHERE status = #{status} AND order_time < #{orderTime}")
    List<Orders> getByStatusAndCreateTime(Integer status, LocalDateTime orderTime);
}
