package com.chenling.miniprogram.business.mongo.service.impl;

import com.chenling.miniprogram.business.enums.TaskStatus;
import com.chenling.miniprogram.business.mongo.entity.TaskMessageData;
import com.chenling.miniprogram.business.mongo.service.ITaskMessageDataService;
import com.chenling.miniprogram.business.rabbitmq.dto.TaskMessageDTO;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskMessageDataService implements ITaskMessageDataService {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public boolean upsertTaskMessageData(TaskMessageDTO taskMessage) {

    Query query = new Query(Criteria.where("taskId").is(taskMessage.getTaskId()));
    Update update = new Update()
        .set("taskId", taskMessage.getTaskId())
        .set("task_name", taskMessage.getTaskName())
        .set("sponsor", taskMessage.getSponsor())
        .set("client", taskMessage.getClient())
        .set("start_date", taskMessage.getStartDate())
        .set("end_date", taskMessage.getEndDate())
        .set("regulation", taskMessage.getRegulation())
        .set("report_id", taskMessage.getReportId())
        .set("task_status", taskMessage.getTaskStatus());

    TaskMessageData taskMessageData = new TaskMessageData();
    BeanUtils.copyProperties(taskMessage, taskMessageData);

    log.info(taskMessageData.toString());
    mongoTemplate.upsert(query, update, TaskMessageData.class);
    return true;
  }

  @Override
  public Optional<TaskMessageData> queryTaskMessageByTaskId(String taskId) {
    Query query = new Query()
        .addCriteria(Criteria.where("taskId").is(taskId));

    return Optional.ofNullable(mongoTemplate.findOne(query, TaskMessageData.class));
  }

  @Override
  public List<TaskMessageData> queryTaskMessageByTaskStatus(TaskStatus status) {
    return Collections.emptyList();
  }
}
