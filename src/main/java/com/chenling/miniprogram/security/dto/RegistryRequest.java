package com.chenling.miniprogram.security.dto;

import lombok.Data;

@Data
public class RegistryRequest {
  private String username;

  private String password;

  private String displayName;

  private String phone;

  private String company;
}
