package org.zewang.ordersystem.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.zewang.ordersystem.entity.order.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    IPage<Order> selectOrdersByUserIdAndStatus(Page<Order> page, @Param("userId") Long userId, @Param("status") String status);
}
