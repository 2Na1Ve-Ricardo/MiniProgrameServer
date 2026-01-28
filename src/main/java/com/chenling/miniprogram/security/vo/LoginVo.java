package com.chenling.miniprogram.security.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {
  private String token;

  private Long userId;

  private String username;

  private String displayName;

  private String role;

  private String roleName;

  private Boolean hasBindAppId;
}
