package org.zewang.ordersystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.zewang.ordersystem.entity.user.Permission;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    @Select("SELECT p.* FROM permissions p " +
        "JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
        "JOIN user_roles ur ON rp.role_id = ur.role_id " +
        "WHERE ur.user_id = #{userId}")
    List<Permission> findPermissionsByUserId(Long userId);
}
