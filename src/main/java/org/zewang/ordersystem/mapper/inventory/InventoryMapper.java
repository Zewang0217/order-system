package org.zewang.ordersystem.mapper.inventory;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers.Base;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zewang.ordersystem.entity.inventory.Inventory;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 19:15
 */

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
    @Update("UPDATE inventory SET available_stock = available_stock - #{quantity}, " +
        "total_stock = total_stock - #{quantity}, version = version + 1 " +
        "WHERE product_id = #{productId} AND available_stock >= #{quantity} AND version = #{version}")
    int deductStock(@Param("productId") String productId,
        @Param("quantity") Integer quantity,
        @Param("version") Integer version);

    @Select("SELECT * FROM inventory WHERE product_id = #{productId}")
    Inventory selectBuProductId(@Param("productId") String productId);

    @Update("UPDATE inventory SET available_stock = #{inventory.availableStock}, " +
        "total_stock = #{inventory.totalStock}, version = version + 1 " +
        "WHERE product_id = #{inventory.productId} AND version = #{inventory.version}")
    int updateByProductId(@Param("inventory") Inventory inventory);

}
