package com.chenling.miniprogram.config.Properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mini-program-server.services.mongodb")
public class MongoDBProperties {

  private boolean enabled = false;

  @Value("${mini-program-server.services.mongodb.host}")
  private String host;

  @Value("${mini-program-server.services.mongodb.port}")
  private int port;

  @Value("${mini-program-server.services.mongodb.database}")
  private String database;

  private int connectTimeout = 10000;

  private int readTimeout = 30000;

  private int maxPoolSize = 100;

  private int minPoolSize = 10;
}
