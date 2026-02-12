package com.chenling.miniprogram.security.dto;

import lombok.Data;

@Data
public class LoginRequest {

  private String username;

  private String password;

  private String appId;
}
