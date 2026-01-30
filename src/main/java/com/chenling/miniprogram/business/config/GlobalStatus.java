package com.chenling.miniprogram.business.config;

import com.chenling.miniprogram.business.mongo.service.IGlobalCacheService;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalStatus {

  @Resource
  IGlobalCacheService globalCacheService;

  private final AtomicReference<String> currentTaskId = new AtomicReference<>();
  private final AtomicReference<String> currentConditionName = new AtomicReference<>();

  @PostConstruct
  public void init() {
    statusLoader();
    log.info("全局状态已初始化: taskId = {}, conditionName = {}", currentTaskId.get(), currentConditionName.get());
  }

  public void statusLoader() {
    globalCacheService.getGlobalStateCache().ifPresent(cache -> {
      currentTaskId.set(cache.getCurrentTaskId());
      currentConditionName.set(cache.getCurrentConditionName());
    });
  }

  public boolean updateStatus(String taskId, String conditionName) {

    String oldTaskId = currentTaskId.get();
    String oldConditionName = currentConditionName.get();

    if (!isInitialized()) {
      boolean update = globalCacheService.compareAndUpdateCache(taskId, conditionName);
      if (update) {
        currentTaskId.set(taskId);
        currentConditionName.set(conditionName);
      }
      return update;
    }

    boolean taskChanged = !oldTaskId.equals(taskId);
    boolean conditionChanged = !oldConditionName.equals(conditionName);

    if (!taskChanged && !conditionChanged) {
      log.debug("未发生状态变化，跳过更新");
      return false;
    }

    boolean update = globalCacheService.compareAndUpdateCache(taskId, conditionName);
    if (update) {
      currentTaskId.set(taskId);
      currentConditionName.set(conditionName);
    }

    return update;
  }

  public void reset(){
    globalCacheService.resetGlobalCache();
    currentTaskId.set(null);
    currentConditionName.set(null);
    log.info("全局状态已重制");
  }

  public String getCurrentTaskId() {
    return currentTaskId.get();
  }


  public String getCurrentConditionName() {
    return currentConditionName.get();
  }


  public boolean isInitialized() {
    return currentTaskId.get() != null && currentConditionName.get() != null;
  }
}
