package com.chenling.miniprogram.business.mongo.service.impl;

import com.chenling.miniprogram.business.mongo.entity.DeadLetterMessage;
import com.chenling.miniprogram.business.mongo.service.IDeadLetterService;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeadLetterService implements IDeadLetterService {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public DeadLetterMessage saveDeadLetterMessage(Message message, String failureReason) {
    MessageProperties props = message.getMessageProperties();

    List<Map<String, Object>> xDeathHeader = props.getHeader("x-death");
    String originalQueue = null;
    String originalExchange = null;
    String originalRoutingKey = null;
    Long retryCount = 0L;

    // Extracts original queue details from message headers
    if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
      Map<String, Object> deathInfo = xDeathHeader.get(0);
      originalQueue = (String) deathInfo.get("queue");
      originalExchange = (String) deathInfo.get("exchange");

      // routing-keys 是一个 List
      List<String> routingKeys = (List<String>) deathInfo.get("routing-keys");
      if (routingKeys != null && !routingKeys.isEmpty()) {
        originalRoutingKey = routingKeys.get(0);
      }
      retryCount = (Long) deathInfo.get("count");
    }

    // 过滤掉不需要存储的头信息
    Map<String, Object> filteredHeaders = new HashMap<>();
    if (props.getHeaders() != null) {
      props.getHeaders().forEach((k, v) -> {
        if (!k.startsWith("x-death")) {
          filteredHeaders.put(k, v != null ? v.toString() : null);
        }
      });
    }

    DeadLetterMessage deadLetter = new DeadLetterMessage()
        .setOriginalQueue(originalQueue)
        .setOriginalExchange(originalExchange)
        .setOriginalRoutingKey(originalRoutingKey)
        .setMessageBody(new String(message.getBody(), StandardCharsets.UTF_8))
        .setFailureReason(failureReason)
        .setHeaders(filteredHeaders)
        .setRetryCount(retryCount != null ? retryCount.intValue() : 0)
        .setStatus("PENDING")
        .setOriginalTimestamp(props.getTimestamp())
        .setReceivedAt(new Date());

    DeadLetterMessage saved = mongoTemplate.save(deadLetter);
    log.info("死信消息已存储到 MongoDB, id={}, originalQueue={}", saved.getId(), originalQueue);

    return saved;
  }

  @Override
  public long countPending() {
    Query query = new Query(Criteria.where("status").is("PENDING"));
    return mongoTemplate.count(query, DeadLetterMessage.class);
  }
}
