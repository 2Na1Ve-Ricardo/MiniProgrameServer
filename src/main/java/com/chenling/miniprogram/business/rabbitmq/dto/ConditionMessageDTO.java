package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConditionMessageDTO {

  @JsonProperty("SerialNumber")
  private String serialNumber;

  @JsonProperty("TensileForce")
  private String tensileForce;

  @JsonProperty("Curvature")
  private String curvature;

  @JsonProperty("Progress")
  private String progress;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("CycleTimes")
  private String cycleTimes;
}
