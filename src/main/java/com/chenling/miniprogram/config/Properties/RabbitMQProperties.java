package com.chenling.miniprogram.config.Properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mini-program-server.services.rabbitmq")
public class RabbitMQProperties {

  private boolean enabled = false;

  private String host;

  private int port = 5672;

  private String username;

  private String password;

  private String virtualHost;

  private int connectionTimeout = 15000;

  private ExchangeConfig exchange = new ExchangeConfig();

  private List<QueueConfig> queues = new ArrayList<>();

  private DeadLetterConfig deadLetter = new DeadLetterConfig();

  private ListenerConfig listener = new ListenerConfig();

  @Data
  public static class ExchangeConfig {
    private String name = "";
    private String type = "topic";
    private boolean durable = false;
  }

  @Data
  public static class QueueConfig {
    private String name;
    private String routingKey;
    private String deadLetterRoutingKey;
    private boolean durable;
  }

  @Data
  public static class DeadLetterConfig {
    private String exchangeName;
    private String queueName;
    private boolean durable;
  }

  @Data
  public static class ListenerConfig {
    private SimpleConfig simple = new SimpleConfig();

    @Data
    public static class SimpleConfig {
      private String acknowledgeMode;
      private int concurrency;
      private int maxConcurrency;
      private int prefetch = 1;
      private RetryConfig retry = new RetryConfig();

      @Data
      public static class RetryConfig {
        private boolean enabled = true;
        private int maxAttempts;
        private long initialInterval;
        private double multiplier;
        private long maxInterval;
      }
    }
  }
}
