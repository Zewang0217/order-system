package org.zewang.ordersystem.mapper.inventory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zewang.ordersystem.entity.mq.DeadLetterMessage;

@Mapper
public interface DeadLetterMapper extends BaseMapper<DeadLetterMessage> {
}