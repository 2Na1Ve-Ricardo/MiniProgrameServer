package com.chenling.miniprogram.business.rabbitmq.listener;

import com.chenling.miniprogram.business.config.GlobalStatus;
import com.chenling.miniprogram.business.mongo.entity.GlobalStateCache;
import com.chenling.miniprogram.business.mongo.service.IGlobalCacheService;
import com.chenling.miniprogram.business.mongo.service.impl.TaskMessageDataService;
import com.chenling.miniprogram.business.rabbitmq.dto.MqMessageDTO;
import com.chenling.miniprogram.business.rabbitmq.dto.TaskMessageDTO;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import com.chenling.miniprogram.services.rabbitmq.listener.BaseMessageListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
  private TaskMessageDataService taskMessageDataService;

  private  final ObjectMapper objectMapper = new ObjectMapper();

  @RabbitListener(queues = "cache_task")
  public void handleCacheTask(Message message, Channel channel){
    log.info("收到 cache_task 消息");
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_task 的信息: \n {}", body);
      // todo 任务消息队列的具体逻辑
      String receiveCurrentTaskId = objectMapper
          .readValue(body, MqMessageDTO.class)
          .getTaskId();

      String receiveCurrentConditionName = objectMapper
          .readValue(body, MqMessageDTO.class)
          .getConditionName();

      TaskMessageDTO taskMessage = objectMapper
          .readValue(body, MqMessageDTO.class)
          .getTaskMessageData();

      globalStatus.updateStatus(receiveCurrentTaskId, receiveCurrentConditionName);
      log.info(globalStatus.toString());

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

      // todo condition消息队列的具体逻辑
    });
  }


  @RabbitListener(queues = "cache_channel")
  public void handleCacheChannel(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_channel 的信息: \n {}", body);

      // todo 通道消息队列的具体逻辑
    });
  }


  @RabbitListener(queues = "task_data")
  public void handleCacheData(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 task_data 的信息: \n {}", body);

      // todo 数据消息队列的具体逻辑
    });
  }
}
