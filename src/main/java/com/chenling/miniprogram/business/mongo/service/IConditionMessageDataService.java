package com.chenling.miniprogram.business.mongo.service;

import com.chenling.miniprogram.business.mongo.entity.ConditionMessageData;
import com.chenling.miniprogram.business.rabbitmq.dto.ConditionMessageDTO;
import java.util.List;

public interface IConditionMessageDataService {

  /**
   * 插入或更新 condition 数据
   * @param conditionMessage 从MQ获取到的 condition 消息实体
   * @return boolean 是否成功
   */
  boolean upsert(ConditionMessageDTO conditionMessage);

  /**
   * 根据 taskId 和 conditionName 查询 condition 数据
   * @param taskId 任务 ID
   * @param conditionName condition 名称
   * @return ConditionMessageData condition 数据实体
   */
  ConditionMessageData queryByConditionName(String taskId, String conditionName);

  /**
   * 根据 taskId 查询 condition 数据列表
   * @param taskId 任务 ID
   * @return List<ConditionMessageData> condition 数据实体列表
   */
  List<ConditionMessageData> queryListByTaskId(String taskId);
}
