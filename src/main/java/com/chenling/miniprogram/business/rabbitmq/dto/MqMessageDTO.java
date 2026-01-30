package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class MqMessageDTO {

  @JsonProperty("MessageHeader")
  private String messageHeader;

  @JsonProperty("DateTime")
  private Date dateTime;

  @JsonProperty("TaskID")
  private String taskId;

  @JsonProperty("ConditionName")
  private String conditionName;

  @JsonProperty("DataDic")
  private TaskMessageDTO taskMessageData;

  @JsonProperty("ChannelDataDic")
  private ChannelMessageDTO channelMessageData;

  @JsonProperty("ConditionDataDic")
  private ConditionMessageDTO conditionMessageData;
}
