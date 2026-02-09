package com.chenling.miniprogram.business.mongo.entity;

import java.util.Date;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "data_message_data")
public class DataMessageData {

  @Id
  private long id;

  @Field("task_id")
  private String taskId;

  @Field("condition_name")
  private String conditionName;

  @Field("parameter_no")
  private String parameterNo;

  @Field("timestamp")
  private Date timestamp;

  @Field("value")
  private String value;
}
