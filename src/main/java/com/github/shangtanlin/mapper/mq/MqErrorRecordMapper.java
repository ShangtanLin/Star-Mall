package com.github.shangtanlin.mapper.mq;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.mq.MqErrorRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * MQ 异常记录 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了增删改查（CRUD）能力
 */
@Mapper
public interface MqErrorRecordMapper extends BaseMapper<MqErrorRecord> {
    // 这里暂时不需要写任何 SQL，BaseMapper 已经帮我们搞定了 insert(MqErrorRecord)
}
