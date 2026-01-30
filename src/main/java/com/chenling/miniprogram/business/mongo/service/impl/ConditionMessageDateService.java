package com.chenling.miniprogram.business.mongo.service.impl;

import com.chenling.miniprogram.business.config.GlobalStatus;
import com.chenling.miniprogram.business.mongo.entity.ConditionMessageData;
import com.chenling.miniprogram.business.mongo.entity.GlobalStateCache;
import com.chenling.miniprogram.business.mongo.service.IConditionMessageDataService;
import com.chenling.miniprogram.business.rabbitmq.dto.ConditionMessageDTO;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ConditionMessageDateService implements IConditionMessageDataService {

  @Resource
  private MongoTemplate mongoTemplate;

  @Resource
  private GlobalStatus status;

  @Override
  public boolean upsert(ConditionMessageDTO conditionMessage) {
    Query query = new Query()
        .addCriteria(Criteria.where("taskId").is(status.getCurrentTaskId())
            .and("conditionName").is(conditionMessage.getName()));

    Update update = new Update()
        .set("taskId", status.getCurrentTaskId())
        .set("serial_number", conditionMessage.getSerialNumber())
        .set("name", conditionMessage.getName())
        .set("tensile_force", conditionMessage.getTensileForce())
        .set("curvature", conditionMessage.getCurvature())
        .set("cycle_times", conditionMessage.getCycleTimes())
        .set("progress", conditionMessage.getProgress());

    mongoTemplate.upsert(query, update, ConditionMessageData.class);
    return true;
  }

  @Override
  public ConditionMessageData queryByConditionName(String taskId, String conditionName) {
    return null;
  }

  @Override
  public List<ConditionMessageData> queryListByTaskId(String taskId) {
    return Collections.emptyList();
  }
}
