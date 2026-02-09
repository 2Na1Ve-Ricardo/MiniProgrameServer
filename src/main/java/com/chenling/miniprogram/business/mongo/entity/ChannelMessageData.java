package com.chenling.miniprogram.business.mongo.entity;

import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "channel_message_data")
public class ChannelMessageData {

  @Id
  private long id;

  @Field("task_id")
  private String taskId;

  @Field("condition_name")
  private String conditionName;

  @Field("channel_type")
  private String channelType;

  @Field("name")
  private String name;

  @Field("parameter_no")
  private String parameterNo;

  @Field("unit")
  private String unit;
}
