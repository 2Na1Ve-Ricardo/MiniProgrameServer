package com.chenling.miniprogram.security.service.service_interface;

import com.chenling.miniprogram.security.dto.LoginRequest;
import com.chenling.miniprogram.security.dto.RegistryRequest;
import com.chenling.miniprogram.security.vo.LoginVo;

public interface IAuthenticationService {
  LoginVo login(LoginRequest loginRequest);
  LoginVo register(RegistryRequest registryRequest);
  void bindAppId(String username, String appId);
}
