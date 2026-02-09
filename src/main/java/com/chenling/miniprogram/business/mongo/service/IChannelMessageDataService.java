package com.chenling.miniprogram.business.mongo.service;

import com.chenling.miniprogram.business.enums.ChannelType;
import com.chenling.miniprogram.business.mongo.entity.ChannelMessageData;
import com.chenling.miniprogram.business.rabbitmq.dto.ChannelMessageDTO;
import java.util.List;

public interface IChannelMessageDataService {

  boolean upsert(ChannelMessageDTO channelMessage, String channelType);

  List<ChannelMessageData> queryChannelByTaskIdAndConditionName(String taskId, String conditionName);

  List<ChannelMessageData> queryChannelGroupByChannelType(ChannelType type);
}
