package com.chenling.miniprogram.config;

import com.chenling.miniprogram.config.Properties.RabbitMQProperties;
import com.chenling.miniprogram.config.Properties.RabbitMQProperties.QueueConfig;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "mini-program-server.services.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitMQConfig {

  @Resource
  private RabbitMQProperties properties;

  @PostConstruct
  public void init() {
    log.info("========================================");
    log.info("RabbitMQ 服务已启用");
    log.info("连接地址: {}:{}", properties.getHost(), properties.getPort());
    log.info("虚拟主机: {}", properties.getVirtualHost());
    log.info("交换机: {}", properties.getExchange().getName());
    log.info("死信交换机: {}", properties.getDeadLetter().getExchangeName());
    log.info("配置队列数: {}", properties.getQueues().size());
    log.info("========================================");
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory();
    factory.setHost(properties.getHost());
    factory.setPort(properties.getPort());
    factory.setUsername(properties.getUsername());
    factory.setPassword(properties.getPassword());
    factory.setVirtualHost(properties.getVirtualHost());
    factory.setConnectionTimeout(properties.getConnectionTimeout());

    // 开启发布确认
    factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
    factory.setPublisherReturns(true);

    return factory;
  }

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * RabbitTemplate
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
      MessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    template.setMandatory(true);

    // 消息发送确认回调
    template.setConfirmCallback((correlationData, ack, cause) -> {
      if (!ack) {
        log.error("消息发送到交换机失败: {}", cause);
      }
    });

    // 消息返回回调（路由失败时）
    template.setReturnsCallback(returned -> {
      log.warn("消息路由失败: exchange={}, routingKey={}, replyCode={}, replyText={}",
          returned.getExchange(),
          returned.getRoutingKey(),
          returned.getReplyCode(),
          returned.getReplyText());
    });

    return template;
  }

  /**
   * RabbitAdmin - 用于自动声明队列、交换机、绑定
   */
  @Bean
  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    RabbitAdmin admin = new RabbitAdmin(connectionFactory);
    admin.setAutoStartup(true);
    return admin;
  }

  /**
   * 监听器容器工厂
   */
  @Bean
  public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory,
      MessageConverter messageConverter) {

    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter);

    // 从配置读取监听器参数
    var listenerConfig = properties.getListener().getSimple();
    factory.setConcurrentConsumers(listenerConfig.getConcurrency());
    factory.setMaxConcurrentConsumers(listenerConfig.getMaxConcurrency());
    factory.setPrefetchCount(listenerConfig.getPrefetch());

    // 手动确认模式
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

    return factory;
  }

  /**
   * 死信交换机
   */
  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange(
        properties.getDeadLetter().getExchangeName(),
        properties.getDeadLetter().isDurable(),
        false
    );
  }

  /**
   * 死信队列
   */
  @Bean
  public Queue deadLetterQueue() {
    return new Queue(
        properties.getDeadLetter().getQueueName(),
        properties.getDeadLetter().isDurable()
    );
  }

  /**
   * 死信队列绑定到死信交换机
   */
  @Bean
  public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
    // 使用通配符路由键，接收所有死信
    return BindingBuilder.bind(deadLetterQueue)
        .to(deadLetterExchange)
        .with("#");
  }
  /**
   * 业务交换机
   */
  @Bean
  public DirectExchange businessExchange() {
    return new DirectExchange(
        properties.getExchange().getName(),
        properties.getExchange().isDurable(),
        false
    );
  }

  /**
   * 动态声明队列和绑定
   */
  @Bean
  public QueueDeclarator queueDeclarator(RabbitAdmin rabbitAdmin,
      DirectExchange businessExchange,
      DirectExchange deadLetterExchange) {
    return new QueueDeclarator(rabbitAdmin, businessExchange, deadLetterExchange, properties);
  }

  /**
   * 队列声明器 - 根据配置动态创建队列
   */
  public static class QueueDeclarator {

    public QueueDeclarator(RabbitAdmin rabbitAdmin,
        DirectExchange businessExchange,
        DirectExchange deadLetterExchange,
        RabbitMQProperties properties) {

      for (QueueConfig queueConfig : properties.getQueues()) {
        // 配置队列参数，指向死信交换机
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", deadLetterExchange.getName());
        args.put("x-dead-letter-routing-key", queueConfig.getDeadLetterRoutingKey());

        // 创建队列
        Queue queue = new Queue(
            queueConfig.getName(),
            queueConfig.isDurable(),
            false,
            false,
            args
        );
        rabbitAdmin.declareQueue(queue);

        // 绑定到交换机
        Binding binding = BindingBuilder.bind(queue)
            .to(businessExchange)
            .with(queueConfig.getRoutingKey());
        rabbitAdmin.declareBinding(binding);

        log.info("已声明队列: {} -> routingKey: {}, dlqRoutingKey: {}",
            queueConfig.getName(),
            queueConfig.getRoutingKey(),
            queueConfig.getDeadLetterRoutingKey());
      }
    }
  }
}
