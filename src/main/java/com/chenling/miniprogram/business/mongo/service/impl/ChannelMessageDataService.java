package com.chenling.miniprogram.business.mongo.service.impl;

import com.chenling.miniprogram.business.config.GlobalStatus;
import com.chenling.miniprogram.business.enums.ChannelType;
import com.chenling.miniprogram.business.mongo.entity.ChannelMessageData;
import com.chenling.miniprogram.business.mongo.service.IChannelMessageDataService;
import com.chenling.miniprogram.business.rabbitmq.dto.ChannelMessageDTO;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChannelMessageDataService implements IChannelMessageDataService {

  @Resource
  GlobalStatus globalStatus;

  @Resource
  MongoTemplate mongoTemplate;

  @Override
  public boolean upsert(ChannelMessageDTO channelMessage, String channelType) {
    Query query = new Query()
        .addCriteria(Criteria.where("taskId").is(globalStatus.getCurrentTaskId())
            .and("condition_name").is(globalStatus.getCurrentConditionName()));

    String taskId = globalStatus.getCurrentTaskId();
    String conditionName = globalStatus.getCurrentConditionName();

    if(taskId == null || conditionName == null || taskId.isEmpty() || conditionName.isEmpty()) {
      throw new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "【通道信息存储过程... 】无法获取到当前任务ID或条件名称");
    }

    Update update = new Update()
        .set("task_id", globalStatus.getCurrentTaskId())
        .set("condition_name", globalStatus.getCurrentConditionName())
        .set("channel_type", channelType)
        .set("name", channelMessage.getName())
        .set("parameter_no", channelMessage.getParameterNo())
        .set("unit", channelMessage.getUnit());

    UpdateResult upsert = mongoTemplate.upsert(query, update, ChannelMessageData.class);

    return upsert.wasAcknowledged();
  }

  @Override
  public List<ChannelMessageData> queryChannelByTaskIdAndConditionName(String taskId, String conditionName) {

    return mongoTemplate.find(new Query(Criteria
        .where("task_id").is(taskId)
        .and("condition_name").is(conditionName)),
        ChannelMessageData.class);
  }

  @Override
  public List<ChannelMessageData> queryChannelGroupByChannelType(ChannelType type) {
    return mongoTemplate.find(new Query(Criteria.where("channel_type")
        .is(type.getCode())), ChannelMessageData.class);
  }
}
