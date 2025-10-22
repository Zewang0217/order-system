package org.zewang.ordersystem.mapper.mq;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.zewang.ordersystem.entity.mq.OutboxMessage;

@Mapper
public interface OutboxMapper extends BaseMapper<OutboxMessage> {

    /**
     * 选择待发送或可重试的 outbox 消息：
     * - message_status = 'PENDING'
     * - OR message_status = 'FAILED' AND retry_count < max_retry_count AND 达到指数回退时间
     *
     * 这里使用 last_retry_time（若为 NULL 则使用 created_time）加上 2^{retry_count} 秒作为回退窗口，回退秒数上限由 SQL 中的常量限制。
     */
    @Select("SELECT * FROM outbox_messages " +
        "WHERE message_status = 'PENDING' " +
        "OR (message_status = 'FAILED' " +
        "    AND retry_count < IFNULL(max_retry_count, #{defaultMaxRetry}) " +
        "    AND (last_retry_time IS NULL " +
        "         OR DATE_ADD(COALESCE(last_retry_time, created_time), INTERVAL LEAST(POW(2, IFNULL(retry_count,0)), #{maxBackoff}) SECOND) <= NOW())) " +
        "ORDER BY created_time ASC LIMIT #{limit}")
    List<OutboxMessage> selectPendingMessages(int limit, int defaultMaxRetry, long maxBackoff);

}
