package com.chenling.miniprogram.security.service.service_interface;

import com.chenling.miniprogram.security.dto.LoginRequest;
import com.chenling.miniprogram.security.dto.RegistryRequest;
import com.chenling.miniprogram.security.vo.LoginVo;

public interface IAuthenticationService {
  /**
   * 获取微信openid
   * @param jsCode 前端获取到的微信 jsCode
   * @return openId
   */
  String getWechatOpenid(String jsCode);

  /**
   * 用户登录
   * @param loginRequest 用户登录信息
   * @return 用户信息
   */
  LoginVo login(LoginRequest loginRequest);

  /**
   * 用户注册
   * @param registryRequest 用户注册信息
   * @return 注册成功与否
   */
  boolean register(RegistryRequest registryRequest);

  /**
   * 判断token是否有效
   * @param token token
   * @return token是否有效
   */
  boolean isTokenValid(String token);
}
