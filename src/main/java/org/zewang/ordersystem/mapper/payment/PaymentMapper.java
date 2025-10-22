package org.zewang.ordersystem.mapper.payment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zewang.ordersystem.entity.payment.Payment;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

}
