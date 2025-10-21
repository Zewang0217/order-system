package org.zewang.ordersystem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.zewang.ordersystem.entity.user.User;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:32
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
