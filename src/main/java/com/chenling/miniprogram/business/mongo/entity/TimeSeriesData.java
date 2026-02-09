package com.chenling.miniprogram.business.mongo.entity;

import java.util.Date;
import java.util.Map;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "time_series_data")
@CompoundIndexes({
    @CompoundIndex(name = "idx_task_condtion_time", def = "{'task_id': 1, 'condition': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_task_time", def = "{'task_id': 1, 'timestamp': -1}")
})
public class TimeSeriesData {

  @Id
  private long id;

  @Indexed
  @Field("taskId")
  private String taskId;

  @Indexed
  @Field("conditionName")
  private String conditionName;

  @Field("channelName")
  private String channelName;

  @Field("value")
  private Object value;

  @Indexed
  @Field("timestamp")
  private Long timestamp;

  @Field("metadata")
  private Map<String, Object> metadata;

  @Field
  private Date createAt;
}
