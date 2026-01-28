package com.chenling.miniprogram.security.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
  USER(1L, "ROLE_USER", "普通用户"),
  ADMIN(2L, "ROLE_ADMIN", "管理员"),
  SUPER_ADMIN(3L, "ROLE_SUPER_ADMIN", "超级管理员");

  private final Long id;

  private final String code;

  private final String name;


  RoleEnum(Long id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }
}
