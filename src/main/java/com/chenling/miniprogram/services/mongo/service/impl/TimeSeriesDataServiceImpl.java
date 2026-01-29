package com.chenling.miniprogram.services.mongo.service.impl;

import com.chenling.miniprogram.services.mongo.entity.TimeSeriesData;
import com.chenling.miniprogram.services.mongo.service.ITimeSeriesDataService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnBean(MongoTemplate.class)
public class TimeSeriesDataServiceImpl implements ITimeSeriesDataService {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public TimeSeriesData saveTimeSeriesData(String taskId, String conditionName, Object value,
      Map<String, Object> metadata) {
    return null;
  }

  @Override
  public TimeSeriesData saveTimeSeriesData(String taskId, String conditionName, Object value) {
    return null;
  }

  @Override
  public TimeSeriesData queryLastTimeSeriesData(String taskId, String conditionName,
      String channelName) {
    return null;
  }

  @Override
  public List<TimeSeriesData> queryTimeSeriesData(String taskId, String conditionName,
      String channelName, String startTime, String endTime) {
    return Collections.emptyList();
  }

  @Override
  public List<TimeSeriesData> queryTimeSeriesData(String taskId, String conditionName,
      String channelName) {
    return Collections.emptyList();
  }

  @Override
  public long removeTimeSeriesDataByTaskId(String taskId) {
    return 0;
  }
}
