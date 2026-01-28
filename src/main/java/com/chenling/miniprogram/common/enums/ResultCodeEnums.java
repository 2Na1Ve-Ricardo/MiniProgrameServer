package com.chenling.miniprogram.common.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnums {
  // 成功
  SUCCESS(200, "操作成功"),

  // 客户端错误 4xx
  FAIL(400, "操作失败"),
  BAD_REQUEST(400, "请求参数错误"),
  UNAUTHORIZED(401, "未登录或登录已过期"),
  FORBIDDEN(403, "没有访问权限"),
  NOT_FOUND(404, "资源不存在"),
  METHOD_NOT_ALLOWED(405, "请求方法不支持"),

  // 服务端错误 5xx
  INTERNAL_ERROR(500, "服务器内部错误"),

  // 用户相关 1xxx
  USER_NOT_FOUND(1001, "用户不存在"),
  USER_ALREADY_EXISTS(1002, "用户已存在"),
  USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
  APP_ID_NOT_FOUND(1004, "AppId不存在"),
  APP_ID_ALREADY_EXISTS(1005, "AppId已被注册"),
  TOKEN_INVALID(1006, "Token无效"),
  TOKEN_EXPIRED(1007, "Token已过期"),

  // 参数校验 2xxx
  PARAM_ERROR(2001, "参数校验失败"),
  PARAM_MISSING(2002, "必填参数缺失");

  private final Integer code;
  private final String message;

  ResultCodeEnums(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
