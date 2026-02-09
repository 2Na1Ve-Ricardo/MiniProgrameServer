package com.chenling.miniprogram.business.rabbitmq.listener;

import com.chenling.miniprogram.business.config.GlobalStatus;
import com.chenling.miniprogram.business.enums.ChannelType;
import com.chenling.miniprogram.business.mongo.entity.ChannelMessageData;
import com.chenling.miniprogram.business.mongo.entity.ConditionMessageData;
import com.chenling.miniprogram.business.mongo.service.IChannelMessageDataService;
import com.chenling.miniprogram.business.mongo.service.IConditionMessageDataService;
import com.chenling.miniprogram.business.mongo.service.ITaskMessageDataService;
import com.chenling.miniprogram.business.rabbitmq.dto.ChannelMessageDTO;
import com.chenling.miniprogram.business.rabbitmq.dto.ConditionMessageDTO;
import com.chenling.miniprogram.business.rabbitmq.dto.MqMessageDTO;
import com.chenling.miniprogram.business.rabbitmq.dto.TaskDataDTO;
import com.chenling.miniprogram.business.rabbitmq.dto.TaskMessageDTO;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import com.chenling.miniprogram.services.rabbitmq.listener.BaseMessageListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "mini-program-server.services.rabbitmq",
    name = "enabled",
    havingValue = "true"
)
public class MessageListener extends BaseMessageListener {

  @Resource
  private GlobalStatus globalStatus;

  @Resource
  private ITaskMessageDataService taskMessageDataService;

  @Resource
  private IConditionMessageDataService conditionMessageDataService;

  @Resource
  private IChannelMessageDataService channelMessageDataService;

  private  final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private MongoTemplate mongoTemplate;

  @RabbitListener(queues = "cache_task")
  public void handleCacheTask(Message message, Channel channel){
    log.info("收到 cache_task 消息");
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_task 的信息: \n {}", body);
      updateCache(body);
      TaskMessageDTO taskMessage = objectMapper.readValue(body, MqMessageDTO.class).getTaskMessageData();
      boolean isUpsert = taskMessageDataService.upsertTaskMessageData(taskMessage);

      if (isUpsert) {
        log.info("任务消息数据更新成功");
      } else {
        log.info("任务消息数据更新失败");
      }
    });
  }


  @RabbitListener(queues = "cache_condition")
  public void handleCacheCondition(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_condition 的信息: \n {}", body);
      updateCache(body);

      Map<String, ConditionMessageDTO> conditionMap = objectMapper.readValue(body, MqMessageDTO.class).getConditionMessageData();

      for (Entry<String, ConditionMessageDTO> entry : conditionMap.entrySet()) {
        ConditionMessageDTO condition = entry.getValue();
        conditionMessageDataService.upsert(condition);
      }
    });
  }


  @RabbitListener(queues = "cache_channel")
  public void handleCacheChannel(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_channel 的信息: \n {}", body);

      updateCache(body);
      Map<String, List<ChannelMessageDTO>> channelMessage = objectMapper.readValue(body, MqMessageDTO.class).getChannelMessageData();
      log.info("channelMessage = {}", channelMessage);

      for (Entry<String, List<ChannelMessageDTO>> entry : channelMessage.entrySet()) {
        if (Objects.equals(entry.getKey(), ChannelType.DEVICE.getType())) {
          for (ChannelMessageDTO channelMessageDTO : entry.getValue()) {
            channelMessageDataService.upsert(channelMessageDTO, ChannelType.DEVICE.getType());
          }
        } else {
          for (ChannelMessageDTO channelMessageDTO : entry.getValue()) {
            channelMessageDataService.upsert(channelMessageDTO, ChannelType.CHANNEL.getType());
          }
        }
      }

    });
  }


  @RabbitListener(queues = "task_data")
  public void handleCacheData(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 task_data 的信息: \n {}", body);

      // todo 数据消息队列的具体逻辑
      Map<String, Object> rawTaskDataMap = objectMapper.readValue(body,
          new TypeReference<Map<String, Object>>() {});

      log.info("rawTaskDataMap = {}", rawTaskDataMap);
      String receiveTaskId = rawTaskDataMap.get("TaskId") == null ? null : rawTaskDataMap.get("TaskId").toString();
      String receiveConditionName = rawTaskDataMap.get("ConditionName") == null ? null : rawTaskDataMap.get("ConditionName").toString();
      Object dataDict = rawTaskDataMap.get("DataDic");

      updateCache(receiveTaskId, receiveConditionName);

      Map<String, Object> dataMapper;
      if (dataDict instanceof Map) {
        dataMapper = (Map<String, Object>) dataDict;
      } else if (dataDict instanceof String) {
        dataMapper = objectMapper.readValue((String) dataDict,
            new TypeReference<Map<String, Object>>() {});
      } else {
        throw new RuntimeException("DataDic 格式不支持: " + (dataDict == null ? "null" : dataDict.getClass().getName()));
      }

      log.info("dataMap => {}", dataMapper);

      dataMapper.forEach((k, jsonValue) -> {
        TaskDataDTO taskData = null;
        try {
          // 修复：根据值的类型决定如何处理
          if (jsonValue instanceof Map) {
            taskData = objectMapper.convertValue(jsonValue, TaskDataDTO.class);
          } else if (jsonValue instanceof String) {
            String jsonStr = (String) jsonValue;
            if (jsonStr.contains("\\\"") || jsonStr.startsWith("\"{")) {
              // 方法1：如果是被引号包裹的转义字符串，先用 ObjectMapper 解析为纯字符串
              jsonStr = objectMapper.readValue(jsonStr, String.class);
            }
            taskData = objectMapper.readValue(jsonStr, TaskDataDTO.class);
          } else {
            log.warn("跳过未知类型的数据: key={}, type={}", k, jsonValue.getClass().getName());
            return;
          }
        } catch (Exception e) {
          throw new RuntimeException("解析 TaskDataDTO 失败: key=" + k, e);
        }
        log.info(taskData.toString());
      });

    });
  }


  private void updateCache(String body) throws JsonProcessingException {
    String receiveCurrentTaskId = objectMapper
        .readValue(body, MqMessageDTO.class)
        .getTaskId();

    String receiveCurrentConditionName = objectMapper
        .readValue(body, MqMessageDTO.class)
        .getConditionName();

    if (receiveCurrentTaskId == null || receiveCurrentConditionName == null || receiveCurrentTaskId.isEmpty() || receiveCurrentConditionName.isEmpty()) {
      throw new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "TaskId 或 ConditionName 不能为空");
    }

    boolean isUpdate = globalStatus.updateStatus(receiveCurrentTaskId, receiveCurrentConditionName);
    log.info("更新状态结果: {}", isUpdate);
  }

  private void updateCache(String receiveTaskId, String receiveConditionName) {
    if (receiveTaskId == null || receiveConditionName == null || receiveTaskId.isEmpty() || receiveConditionName.isEmpty()) {
      throw new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "TaskId 或 ConditionName 不能为空");
    }

    boolean isUpdate = globalStatus.updateStatus(receiveTaskId, receiveConditionName);
    log.info("更新状态结果: {}", isUpdate);
  }
}
