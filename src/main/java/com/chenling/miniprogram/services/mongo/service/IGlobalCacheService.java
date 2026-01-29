package com.chenling.miniprogram.services.mongo.service;

import com.chenling.miniprogram.services.mongo.entity.GlobalStateCache;
import java.util.Optional;

public interface IGlobalCacheService {

  Optional<GlobalStateCache> getGlobalStateCache();
  Optional<String> getCurrentTaskId();
  Optional<String> getCurrentConditionName();

  boolean compareAndUpdateCache(String taskId, String conditionName);

  void resetGlobalCache();
}
