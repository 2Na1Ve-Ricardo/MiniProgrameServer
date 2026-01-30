package com.chenling.miniprogram.business.rabbitmq.listener;

import com.chenling.miniprogram.business.mongo.entity.DeadLetterMessage;
import com.chenling.miniprogram.business.mongo.service.IDeadLetterService;
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
public class DeadLetterListener {

  @Resource IDeadLetterService deadLetterService;

  @RabbitListener(queues = "#{@rabbitMQProperties.deadLetter.queueName}")
  public void handleDeadLetter(Message message, Channel channel) throws Exception{
    long deliveryTag = message.getMessageProperties().getDeliveryTag();

    try {
      String body = new String(message.getBody());
      // 从 x-death 头中获取原始信息
      String originalRoutingKey = null;
      String originalQueue = null;
      String reason = message.getMessageProperties().getHeader("x-dead-letter-reason");

      java.util.List<java.util.Map<String, Object>> xDeathHeader =
          message.getMessageProperties().getHeader("x-death");

      if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
        java.util.Map<String, Object> deathInfo = xDeathHeader.get(0);
        originalQueue = (String) deathInfo.get("queue");
        reason = (String) deathInfo.get("reason");

        java.util.List<String> routingKeys = (java.util.List<String>) deathInfo.get("routing-keys");
        if (routingKeys != null && !routingKeys.isEmpty()) {
          originalRoutingKey = routingKeys.get(0);
        }
      }

      log.error("============ 死信消息 ============");
      log.error("原路由键: {}", originalRoutingKey);
      log.error("失败原因: {}", reason);
      log.error("消息内容: {}", body);
      log.error("=================================");


      DeadLetterMessage savedMessage = deadLetterService.saveDeadLetterMessage(message, reason);
      log.info("死信消息已保存， MongoDB ID: {}, 当前保存死信条数: {}", savedMessage.getId(), deadLetterService.countPending());

      // 确认消息
      channel.basicAck(deliveryTag, false);

    } catch (Exception e) {
      log.error("死信消息处理失败: {}", e.getMessage(), e);
      channel.basicAck(deliveryTag, false);
    }
  }
}
