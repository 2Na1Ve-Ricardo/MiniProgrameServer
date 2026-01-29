package com.chenling.miniprogram.services.mongo.entity;

import java.util.Date;
import javax.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@Document(collection = "global_state_cache")
public class GlobalStateCache {

  public static final String CACHE_KEY = "GLOBAL_STATE_CACHE";

  @Id
  private String id = CACHE_KEY;

  @Field("currentTaskId")
  private String currentTaskId;

  @Field("currentConditionName")
  private String currentConditionName;

  @Field("createAt")
  private Date createAt;


  @Field("updateAt")
  private Date updateAt;

}
