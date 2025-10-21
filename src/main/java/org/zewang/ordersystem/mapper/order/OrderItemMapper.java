package org.zewang.ordersystem.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zewang.ordersystem.entity.order.OrderItem;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
