package com.chenling.miniprogram.business.mongo.entity;

import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "condition_message_data")
public class ConditionMessageData {

  @Id
  private long id;

  @Field("serial_number")
  private String serialNumber;

  @Field("task_id")
  private String taskId;

  @Field("name")
  private String name;

  @Field("tensile_force")
  private String tensileForce;

  @Field("curvature")
  private String curvature;

  @Field("cycle_times")
  private String cycleTimes;

  @Field("progress")
  private String progress;

}
