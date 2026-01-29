package com.chenling.miniprogram.services.rabbitmq;

import com.chenling.miniprogram.config.Properties.RabbitMQProperties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnBean(RabbitTemplate.class)
public class RabbitMQService {

  @Resource
  private RabbitTemplate rabbitTemplate;

  @Resource
  private RabbitMQProperties properties;

  /**
   * 发送消息到指定路由键
   * @param routingKey 路由键
   * @param message 消息内容
   */
  public void sendMessage(String routingKey, Object message) {
    rabbitTemplate.convertAndSend(
        properties.getExchange().getName(),
        routingKey,
        message
    );

    log.info("Message sent to routing key: {}, message: {}", routingKey, message);
  }


  /**
   * 发松消息到死信队列
   * @param message 原始消息
   * @param reason 进入死信的原因
   */
  public void sendToDeadLetterQueue(Message message, String reason) {
    String dlqExchange = properties.getDeadLetter().getExchangeName();
    String dlqRoutingKey = properties.getDeadLetter().getQueueName();

    message.getMessageProperties().setHeader("x-dead-letter-reason", reason);
    message.getMessageProperties().setHeader("x-original-routing-key", message.getMessageProperties().getReceivedRoutingKey());
    message.getMessageProperties().setHeader("x-death-time", System.currentTimeMillis());

    rabbitTemplate.send(dlqExchange, dlqRoutingKey, message);
    log.warn("消息: {}已发送到死信队列： reason={}",message.getBody(), reason);
  }

  /**
   * 发送消息到死信队列
   * @param message 原始消息
   * @param deadLetterRoutingKey 路由键
   * @param reason 进入死信的原因
   */
  public void sendToDeadLetterQueue(Object message, String deadLetterRoutingKey, String reason) {
    rabbitTemplate.convertAndSend(
        properties.getDeadLetter().getExchangeName(),
        deadLetterRoutingKey,
        message,
        msg -> {
          msg.getMessageProperties().setHeader("x-dead-letter-reason", reason);
          msg.getMessageProperties().setHeader("x-death-time", System.currentTimeMillis());
          return msg;
        }
    );

    log.warn("消息: {}已发送到死信队列： reason={}, routingKey={}",message, reason, deadLetterRoutingKey);
  }
}
