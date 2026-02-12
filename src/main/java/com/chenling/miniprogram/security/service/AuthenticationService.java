package com.chenling.miniprogram.security.service;

import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import com.chenling.miniprogram.security.components.JwtUtils;
import com.chenling.miniprogram.security.dto.LoginRequest;
import com.chenling.miniprogram.security.dto.RegistryRequest;
import com.chenling.miniprogram.security.dto.WeChatLoginDTO;
import com.chenling.miniprogram.security.entity.Role;
import com.chenling.miniprogram.security.entity.User;
import com.chenling.miniprogram.security.enums.RoleEnum;
import com.chenling.miniprogram.security.mapper.IRoleRepository;
import com.chenling.miniprogram.security.mapper.IUserRepository;
import com.chenling.miniprogram.security.service.service_interface.IAuthenticationService;
import com.chenling.miniprogram.security.vo.LoginVo;
import javax.annotation.Resource;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class AuthenticationService implements IAuthenticationService {

  private final String APP_ID;

  private final String APP_SECRET;

  @Resource private IUserRepository userRepository;

  @Resource private IRoleRepository roleRepository;

  @Resource private PasswordEncoder passwordEncoder;

  @Resource private JwtUtils jwtUtils;

  @Resource private CustomUserDetailService userDetailService;

  @Resource private ObjectMapper objectMapper;

  @Resource private RestTemplate restTemplate;

  @Resource private BCryptPasswordEncoder encoder;

  public AuthenticationService(
      @Value("${server-config.wechat.appId}") String appId,
      @Value("${server-config.wechat.secret}") String appSecret) {
    this.APP_ID = appId;
    this.APP_SECRET = appSecret;
  }

  @Override
  public String getWechatOpenid(String jsCode) {
    String requestUrl =
            UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                    .queryParam("appid", APP_ID)
                    .queryParam("secret", APP_SECRET)
                    .queryParam("js_code", jsCode)
                    .queryParam("grant_type", "authorization_code")
                    .toUriString();

    String jsonResponse = restTemplate.getForObject(requestUrl, String.class);

    try {
      WeChatLoginDTO wechatLoginResponse = objectMapper.readValue(jsonResponse, WeChatLoginDTO.class);
      if (wechatLoginResponse.getOpenid() == null || wechatLoginResponse.getOpenid().isEmpty()) {
        log.error("获取微信登录信息时候发生错误: {}, 原因: {}", wechatLoginResponse.getErrCode(), wechatLoginResponse.getErrMsg());
        throw new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "获取微信登录信息失败");
      }
      return wechatLoginResponse.getOpenid();
    } catch (Exception e) {
      log.error("解析微信登录响应失败：{}", e.getMessage());
      throw new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "获取微信登录信息失败");
    }
  }

  @Override
  public LoginVo login(LoginRequest request) {
    User user = userDetailService.loadUserByAppId(request.getAppId());

    if (user == null) {
      log.warn("用户未找到，appId: {}", request.getAppId());

      if (request.getUsername() == null || request.getUsername().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()) {
        throw new BusinessException(ResultCodeEnums.PARAM_ERROR, "用户名或密码不能为空");
      }

      user = userRepository
              .findByUsername(request.getUsername())
              .orElseThrow(() -> new BusinessException(ResultCodeEnums.USER_NOT_FOUND, "用户不存在"));

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BusinessException(ResultCodeEnums.USERNAME_OR_PASSWORD_ERROR);
      }

      bindAppId(user.getUsername(), request.getAppId());
    }

    String token = jwtUtils.generateToken(user.getUsername(), user.getRole().getRoleCode());

    return LoginVo.builder()
        .token(token)
        .userId(user.getId())
        .username(user.getUsername())
        .displayName(user.getDisplayName())
        .role(user.getRole().getRoleCode())
        .roleName(user.getRole().getRoleName())
        .hasBindAppId(user.getAppId() != null)
        .build();
  }

  @Override
  @Transactional
  public boolean register(RegistryRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BusinessException(ResultCodeEnums.USER_ALREADY_EXISTS, "用户名已存在");
    }

    Role defaultRole =
        roleRepository
            .findById(RoleEnum.USER.getId())
            .orElseThrow(() -> new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "默认角色不存在"));

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(encoder.encode(request.getPassword()));
    user.setDisplayName(request.getDisplayName());
    user.setPhone(request.getPhone());
    user.setCompany(request.getCompany());
    user.setRole(defaultRole);

    user = userRepository.save(user);

    return true;
  }

  @Override
  public boolean isTokenValid(String token) {
    return jwtUtils.validateToken(token);
  }

  private void bindAppId(String username, String appId) {
    if (userRepository.findByAppId(appId).isPresent()) {
      throw new BusinessException(ResultCodeEnums.APP_ID_ALREADY_EXISTS);
    }

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new BusinessException(ResultCodeEnums.USER_NOT_FOUND));

    if (user.getAppId() != null) {
      log.warn("当前用户已经绑定微信ID，跳过绑定");
      return;
    }
    user.setAppId(appId);
    userRepository.save(user);
  }
}
