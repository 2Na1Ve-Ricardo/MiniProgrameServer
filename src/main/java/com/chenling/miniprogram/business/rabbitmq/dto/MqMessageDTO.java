package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
  private Map<String, List<ChannelMessageDTO>> channelMessageData;

  @JsonProperty("ConditionDataDic")
  private Map<String, ConditionMessageDTO> conditionMessageData;
}
