package com.chenling.miniprogram.common.handler;

import com.chenling.miniprogram.common.Response.Results;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * 业务异常
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Results<Void> handleBusinessException (BusinessException e) {
    logger.warn("业务异常: code = {}, message = {}", e.getCode(), e.getMessage());

    return Results.fail(e.getCode(), e.getMessage());
  }

  /**
   * 参数校验异常
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Results<Void> handleValidException(MethodArgumentNotValidException e){
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));
    logger.warn("参数校验失败: {}", message);
    return Results.fail(ResultCodeEnums.PARAM_ERROR, message);
  }

  /**
   * 缺少请求参数
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Results<Void> handleMissingParamException(MissingServletRequestParameterException e) {
    logger.warn("缺少参数: {}", e.getParameterName());
    return Results.fail(ResultCodeEnums.PARAM_MISSING, "缺少参数: " + e.getParameterName());
  }

  /**
   * 请求体解析失败
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Results<Void> handleMessageNotReadableException(HttpMessageNotReadableException e) {
    logger.warn("请求体解析失败: {}", e.getMessage());
    return Results.fail(ResultCodeEnums.BAD_REQUEST, "请求体格式错误");
  }

  /**
   * 请求方法不支持
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public Results<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
    logger.warn("不支持的请求方法: {}", e.getMethod());
    return Results.fail(ResultCodeEnums.METHOD_NOT_ALLOWED);
  }

  /**
   * 404 资源不存在
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Results<Void> handleNotFoundException(NoHandlerFoundException e) {
    logger.warn("资源不存在: {}", e.getRequestURL());
    return Results.fail(ResultCodeEnums.NOT_FOUND);
  }

  /**
   * 其他异常
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Results<Void> handleException(Exception e) {
    logger.error("系统异常: ", e);
    return Results.fail(ResultCodeEnums.INTERNAL_ERROR, "服务器内部错误");
  }
}
