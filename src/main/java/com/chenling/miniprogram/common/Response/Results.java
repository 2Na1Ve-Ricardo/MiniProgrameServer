package com.chenling.miniprogram.common.Response;

import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import java.io.Serializable;
import lombok.Data;

@Data
public class Results<T> implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer code;

  private String message;

  private T data;

  private Long timestamp;

  private Results() {
    this.timestamp = System.currentTimeMillis();
  }

  private Results(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
  }

  // ======== 成功 =========
  public static <T> Results<T> success() {
    return new Results<>(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMessage(), null);
  }

  public static <T> Results<T> success(T data) {
    return new Results<>(ResultCodeEnums.SUCCESS.getCode(), ResultCodeEnums.SUCCESS.getMessage(), data);
  }

  public static <T> Results<T> success(String message, T data) {
    return new Results<>(ResultCodeEnums.SUCCESS.getCode(), message, data);
  }

  // ========== 失败 =============
  public static <T> Results<T> fail () {
    return new Results<>(ResultCodeEnums.FAIL.getCode(), ResultCodeEnums.FAIL.getMessage(), null);
  }

  public static <T> Results<T> fail(String message) {
    return new Results<>(ResultCodeEnums.FAIL.getCode(), message, null);
  }

  public static <T> Results<T> fail (Integer code, String message) {
    return new Results<>(code, message, null);
  }

  public static <T> Results<T> fail (ResultCodeEnums resultCodeEnums) {
    return new Results<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), null);
  }

  public static <T> Results<T> fail (ResultCodeEnums resultCodeEnums, String message) {
    return new Results<>(resultCodeEnums.getCode(), message, null);
  }

}
