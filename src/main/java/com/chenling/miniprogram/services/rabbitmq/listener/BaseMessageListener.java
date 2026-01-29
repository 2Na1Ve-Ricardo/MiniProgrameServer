package com.chenling.miniprogram.services.rabbitmq.listener;

import com.chenling.miniprogram.services.rabbitmq.RabbitMQService;
import com.rabbitmq.client.Channel;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

@Slf4j
public abstract class BaseMessageListener {

  @Resource
  protected RabbitMQService rabbitMQService;

  @FunctionalInterface
  protected interface MessageProcessor {
    void process() throws Exception;
  }

  protected void processMessage(Message message, Channel channel, MessageProcessor processor) {
    long deliveryTag = message.getMessageProperties().getDeliveryTag();
    String routingKey = message.getMessageProperties().getReceivedRoutingKey();

    try {
      log.info("开始处理消息，routingKey = {}, deliveryTag = {}", routingKey, deliveryTag);
      processor.process();

      channel.basicAck(deliveryTag, false);
      log.info("消息处理成功，deliveryTag = {}", deliveryTag);

    } catch (Exception e) {
      log.error("消息处理失败：routingKey = {}, error = {}", routingKey, e.getMessage());

      try {
        channel.basicNack(deliveryTag, false, false);

        if (rabbitMQService != null) {
          rabbitMQService.sendToDeadLetterQueue(message, e.getMessage());
        }
      } catch (Exception ex) {
        log.error("消息确认失败: {}", ex.getMessage(), ex);
      }
    }
  }
}
