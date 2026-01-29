package com.chenling.miniprogram.config;

import com.chenling.miniprogram.config.Properties.MongoDBProperties;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "mini-program-server.services.mongodb", name = "enabled", havingValue = "true")
public class MongoDBConfig {

  @Resource
  private MongoDBProperties properties;

  @PostConstruct
  public void init() {
    log.info("========================================");
    log.info("MongoDB 服务已启用");
    log.info("连接地址: {}:{}", properties.getHost(), properties.getPort());
    log.info("数据库名: {}", properties.getDatabase());
    log.info("========================================");
  }

  /**
   * 创建 MongoClient
   * @return MongoClient
   */
  @Bean
  public MongoClient mongoClient() {
    String connectionString = connectionStringBuilder();
    log.debug("MongoDB 连接字符串: {}", connectionString);

    MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(connectionString))
        .applyToConnectionPoolSettings(builder ->  builder
            .maxSize(properties.getMaxPoolSize())
            .minSize(properties.getMinPoolSize())
        )
        .applyToSocketSettings(builder -> builder
            .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
        )
        .build();

    return MongoClients.create(settings);
  }

  /**
   * 创建 MongoDatabaseFactory
   * @param mongoClient MongoClient
   * @return MongoDatabaseFactory
   */
  @Bean
  public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
    return new SimpleMongoClientDatabaseFactory(mongoClient, properties.getDatabase());
  }

  /**
   * 创建 MongoTemplate
   * @param mongoDatabaseFactory MongoDatabaseFactory
   * @return MongoTemplate
   */
  @Bean
  public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
    MappingMongoConverter converter = new MappingMongoConverter(
        new DefaultDbRefResolver(mongoDatabaseFactory),
        new MongoMappingContext()
    );

    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return new MongoTemplate(mongoDatabaseFactory, converter);
  }

  /**
   * 构建 MongoDB 连接字符串
   * @return 连接字符串
   */
  private String connectionStringBuilder() {
    return "mongodb://" + properties.getHost() + ":" + properties.getPort();
  }
}
