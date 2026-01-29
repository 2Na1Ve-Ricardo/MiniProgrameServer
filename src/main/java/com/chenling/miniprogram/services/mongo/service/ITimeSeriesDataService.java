package com.chenling.miniprogram.services.mongo.service;

import com.chenling.miniprogram.services.mongo.entity.TimeSeriesData;
import java.util.List;
import java.util.Map;

public interface ITimeSeriesDataService {

  TimeSeriesData saveTimeSeriesData(String taskId, String conditionName, Object value, Map<String, Object> metadata);
  TimeSeriesData saveTimeSeriesData(String taskId, String conditionName, Object value);
  TimeSeriesData queryLastTimeSeriesData(String taskId, String conditionName, String channelName);

  List<TimeSeriesData> queryTimeSeriesData(String taskId, String conditionName, String channelName, String startTime, String endTime);
  List<TimeSeriesData> queryTimeSeriesData(String taskId, String conditionName, String channelName);

  long removeTimeSeriesDataByTaskId(String taskId);
}
