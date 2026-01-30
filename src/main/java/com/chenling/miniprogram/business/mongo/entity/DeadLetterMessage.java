package com.chenling.miniprogram.business.mongo.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;


@Data
@Accessors(chain = true)
@Document(collection = "dead_letter_messages")
public class DeadLetterMessage {

  @Id
  private String id;

  /**
   * 原始队列名称
   */
  @Field("originalQueue")
  @Indexed
  private String originalQueue;

  /**
   * 原始路由键
   */
  @Field("originalRoutingKey")
  private String originalRoutingKey;

  /**
   * 原始交换机
   */
  @Field("originalExchange")
  private String originalExchange;

  /**
   * 消息内容（原始字符串）
   */
  @Field("messageBody")
  private String messageBody;

  /**
   * 失败原因
   */
  @Field("failureReason")
  private String failureReason;

  /**
   * 异常类型
   */
  @Field("exceptionType")
  private String exceptionType;

  /**
   * 消息头信息
   */
  @Field("headers")
  private Map<String, Object> headers;

  /**
   * 重试次数
   */
  @Field("retryCount")
  private Integer retryCount;

  /**
   * 消息状态：PENDING（待处理）, PROCESSED（已处理）, IGNORED（已忽略）
   */
  @Field("status")
  @Indexed
  private String status = "PENDING";

  /**
   * 原始消息时间戳
   */
  @Field("originalTimestamp")
  private Date originalTimestamp;

  /**
   * 死信接收时间
   */
  @Field("receivedAt")
  @Indexed
  private Date receivedAt;

  /**
   * 处理时间
   */
  @Field("processedAt")
  private Date processedAt;

  /**
   * 处理备注
   */
  @Field("processNote")
  private String processNote;
}