package com.chenling.miniprogram.common.exceptions;

import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final Integer code;
  private final String message;

  public BusinessException(String message) {
    super(message);
    this.code = ResultCodeEnums.FAIL.getCode();
    this.message = message;
  }

  public BusinessException(Integer code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public BusinessException(ResultCodeEnums resultCode) {
    super(resultCode.getMessage());
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
  }

  public BusinessException(ResultCodeEnums resultCode, String message) {
    super(message);
    this.code = resultCode.getCode();
    this.message = message;
  }
}
