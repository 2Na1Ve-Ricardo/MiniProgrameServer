package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class ChannelMessageDTO {

  @JsonProperty("Name")
  private String name;

  @JsonProperty("ParameterNo")
  private String parameterNo;

  @JsonProperty("Unit")
  private String unit;

  private Date timestamp;

  @JsonProperty("Value")
  private String value;

  @JsonProperty("BeyondMark")
  private String beyondMark;
}
