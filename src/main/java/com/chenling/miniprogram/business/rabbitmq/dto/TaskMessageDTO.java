package com.chenling.miniprogram.business.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class TaskMessageDTO {

  @JsonProperty("TaskName")
  private String taskName;

  @JsonProperty("TaskId")
  private String taskId;

  @JsonProperty("Sponsor")
  private String sponsor;

  @JsonProperty("Client")
  private String client;

  @JsonProperty("StartDate")
  private Date startDate;

  @JsonProperty("EndDate")
  private Date EndDate;

  @JsonProperty("Regulation")
  private String regulation;

  @JsonProperty("ReportId")
  private String reportId;

  @JsonProperty("TaskStatus")
  private String taskStatus;
}
