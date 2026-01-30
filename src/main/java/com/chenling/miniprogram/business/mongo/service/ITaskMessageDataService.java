package com.chenling.miniprogram.business.mongo.service;

import com.chenling.miniprogram.business.enums.TaskStatus;
import com.chenling.miniprogram.business.mongo.entity.TaskMessageData;
import com.chenling.miniprogram.business.rabbitmq.dto.TaskMessageDTO;
import java.util.List;
import java.util.Optional;

public interface ITaskMessageDataService {
  boolean upsertTaskMessageData(TaskMessageDTO taskMessage);

  Optional<TaskMessageData> queryTaskMessageByTaskId(String taskId);

  List<TaskMessageData> queryTaskMessageByTaskStatus(TaskStatus status);
}
