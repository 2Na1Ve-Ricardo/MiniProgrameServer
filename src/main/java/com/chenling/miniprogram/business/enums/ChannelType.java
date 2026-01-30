package com.chenling.miniprogram.business.enums;

import lombok.Getter;

@Getter
public enum ChannelType {
  DEVICE(0, "device"),
  CHANNEL(1, "channel");

  private final int code;
  private final String type;

  ChannelType(int code, String type) {
    this.code = code;
    this.type = type;
  }
}
