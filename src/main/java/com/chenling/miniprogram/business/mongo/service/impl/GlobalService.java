package com.chenling.miniprogram.business.mongo.service.impl;


import com.chenling.miniprogram.business.mongo.entity.GlobalStateCache;
import com.chenling.miniprogram.business.mongo.service.IGlobalCacheService;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GlobalService implements IGlobalCacheService {

  @Resource
  private MongoTemplate mongoTemplate;

  private final String CACHE_KEY = GlobalStateCache.CACHE_KEY;

  /**
   * 获取全局缓存状态
   * @return 全局状态缓存 currentTask， currentConditionName
   */
  @Override
  public Optional<GlobalStateCache> getGlobalStateCache() {
    return Optional.ofNullable(mongoTemplate.findById(CACHE_KEY, GlobalStateCache.class));
  }

  /**
   * 获取当前 taskId
   * @return 当前任务ID
   */
  @Override
  public Optional<String> getCurrentTaskId() {
    return getGlobalStateCache().map(GlobalStateCache::getCurrentTaskId);
  }

  /**
   * 获取当前 conditionName
   * @return 当前条件名称
   */
  @Override
  public Optional<String> getCurrentConditionName() {
    return getGlobalStateCache().map(GlobalStateCache::getCurrentConditionName);
  }

  /**
   * 比较并更新全局缓存状态
   * @param taskId 任务ID
   * @param conditionName 条件名称
   * @return 是否成功更新缓存
   */
  @Override
  public boolean compareAndUpdateCache(String taskId, String conditionName) {
    Optional<GlobalStateCache> currentStatus = getGlobalStateCache();

    if (currentStatus.isPresent()) {
      GlobalStateCache cache = currentStatus.get();
      boolean taskIdSame = cache.getCurrentTaskId().equals(taskId);
      boolean conditionNameSame = cache.getCurrentConditionName().equals(conditionName);

      if (taskIdSame && conditionNameSame) {
        log.debug("当前缓存无需更新");
        return false;
      }
    }

    Date now = new Date();
    Query query = new Query(Criteria.where("_id").is(CACHE_KEY));
    Update update = new Update()
        .set("currentTaskId", taskId)
        .set("currentConditionName", conditionName)
        .set("updateTime", now)
        .setOnInsert("createAt", now);

    mongoTemplate.upsert(query, update, GlobalStateCache.class);
    log.info("全局缓存已更新: taskId = {}, conditionName = {}", taskId, conditionName);

    return true;
  }

  /**
   * 重置全局缓存状态
   */
  @Override
  public void resetGlobalCache() {
    mongoTemplate.remove(new Query(Criteria.where("_id").is(CACHE_KEY)), GlobalStateCache.class);
    log.info("全局缓存已重置");
  }
}
