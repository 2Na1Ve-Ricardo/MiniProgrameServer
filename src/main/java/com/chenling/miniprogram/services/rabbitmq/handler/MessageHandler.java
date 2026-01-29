package com.chenling.miniprogram.services.rabbitmq.handler;

/**
 * 消息处理接口
 * @param <T> 消息类型
 */
public interface MessageHandler<T> {

  /**
   * 处理消息
   * @param message 消息内容
   * @throws Exception 处理失败时抛出异常，消息将会被推送到死信队列
   */
  void handle(T message) throws Exception;

  /**
   * 获取处理器支持的消息类型
   */
  Class<T> getMessageType();
}
