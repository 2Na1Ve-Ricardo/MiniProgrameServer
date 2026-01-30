package com.chenling.miniprogram.business.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
  IN_PROCESS("进行中"),
  FINISHED("已完成");

  private final String status;

  TaskStatus(String status) {
    this.status = status;
  }
}
