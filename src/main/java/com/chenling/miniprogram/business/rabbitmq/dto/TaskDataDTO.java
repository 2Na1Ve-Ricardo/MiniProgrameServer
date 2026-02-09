package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskDataDTO {
  @JsonProperty("Name")
  private String name;

  @JsonProperty("ParameterNo")
  private String parameterNo;

  @JsonProperty("timeStamp")
  private String timestamp;

  @JsonProperty("Unit")
  private String unit;

  @JsonProperty("Value")
  private String value;

  @JsonProperty("BeyondMark")
  private String beyondMark;
}
