package com.chenling.miniprogram.services.rabbitmq.listener;

import com.chenling.miniprogram.services.mongo.service.IGlobalCacheService;
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
public class MessageListener extends BaseMessageListener{

  @Resource
  private IGlobalCacheService globalCacheService;

  private  final ObjectMapper objectMapper = new ObjectMapper();

  @RabbitListener(queues = "cache_task")
  public void handleCacheTask(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_task 的信息: \n {}", body);

      // todo 任务消息队列的具体逻辑
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


  @RabbitListener(queues = "cache_data")
  public void handleCacheData(Message message, Channel channel){
    processMessage(message, channel, () -> {
      String body = new String(message.getBody());
      log.info("收到 cache_data 的信息: \n {}", body);

      // todo 数据消息队列的具体逻辑
    });
  }
}
