package com.chenling.miniprogram.business.mongo.entity;

import java.util.Date;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "task_message_data")
public class TaskMessageData {

  @Id
  private long id;

  @Field("taskId")
  private String taskId;

  @Field("task_name")
  private String taskName;

  @Field("sponsor")
  private String sponsor;

  @Field("client")
  private String client;

  @Field("start_date")
  private Date startDate;

  @Field("end_date")
  private Date endDate;

  @Field("regulation")
  private String regulation;

  @Field("report_id")
  private String reportId;

  @Field("task_status")
  private String taskStatus;
}
