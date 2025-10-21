package org.zewang.ordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.zewang.ordersystem.entity.user.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT r.* FROM roles r " +
        "JOIN user_roles ur ON r.role_id = ur.role_id " +
        "WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(Long userId);
}
