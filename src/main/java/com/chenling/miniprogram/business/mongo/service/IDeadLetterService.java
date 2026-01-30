package com.chenling.miniprogram.business.mongo.service;

import com.chenling.miniprogram.business.mongo.entity.DeadLetterMessage;
import org.springframework.amqp.core.Message;

public interface IDeadLetterService {
  DeadLetterMessage saveDeadLetterMessage(Message message, String failureReason);

  long countPending();
}
