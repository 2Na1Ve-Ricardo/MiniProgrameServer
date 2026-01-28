package com.chenling.miniprogram.security.service;

import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.common.exceptions.BusinessException;
import com.chenling.miniprogram.security.components.JwtUtils;
import com.chenling.miniprogram.security.dto.LoginRequest;
import com.chenling.miniprogram.security.dto.RegistryRequest;
import com.chenling.miniprogram.security.entity.Role;
import com.chenling.miniprogram.security.entity.User;
import com.chenling.miniprogram.security.enums.RoleEnum;
import com.chenling.miniprogram.security.mapper.IRoleRepository;
import com.chenling.miniprogram.security.mapper.IUserRepository;
import com.chenling.miniprogram.security.service.service_interface.IAuthenticationService;
import com.chenling.miniprogram.security.vo.LoginVo;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements IAuthenticationService {

  @Resource
  private IUserRepository userRepository;

  @Resource
  private IRoleRepository roleRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private JwtUtils jwtUtils;

  @Resource
  private CustomUserDetailService userDetailService;

  @Override
  public LoginVo login(LoginRequest request) {
    User user;
    if ("APP_ID".equalsIgnoreCase(request.getLoginType())) {
      if (request.getAppId() == null || request.getAppId().isEmpty()) {
        throw new BusinessException(ResultCodeEnums.PARAM_ERROR, "APP ID 不能为空");
      }
      user = userDetailService.loadUserByAppId(request.getAppId());
    } else {
      if (request.getUsername() == null || request.getUsername().isEmpty() ||
          request.getPassword() == null || request.getPassword().isEmpty()) {
        throw new BusinessException(ResultCodeEnums.PARAM_ERROR, "用户名或密码不能为空");
      }

      user = userRepository.findByUsername(request.getUsername())
          .orElseThrow(() -> new BusinessException(ResultCodeEnums.USER_NOT_FOUND, "用户不存在"));

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BusinessException(ResultCodeEnums.USERNAME_OR_PASSWORD_ERROR);
      }
    }

    String toke = jwtUtils.generateToken(user.getUsername(), user.getRole().getRoleCode());

    return LoginVo.builder()
        .token(toke)
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
  public LoginVo register(RegistryRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BusinessException(ResultCodeEnums.USER_ALREADY_EXISTS, "用户名已存在");
    }

    Role defaultRole = roleRepository.findById(RoleEnum.USER.getId())
        .orElseThrow(() -> new BusinessException(ResultCodeEnums.INTERNAL_ERROR, "默认角色不存在"));

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(request.getPassword());
    user.setDisplayName(request.getDisplayName());
    user.setPhone(request.getPhone());
    user.setCompany(request.getCompany());
    user.setRole(defaultRole);

    user = userRepository.save(user);

    String token = jwtUtils.generateToken(user.getUsername(), defaultRole.getRoleCode());

    return LoginVo.builder()
        .token(token)
        .userId(user.getId())
        .username(user.getUsername())
        .displayName(user.getDisplayName())
        .role(defaultRole.getRoleCode())
        .roleName(defaultRole.getRoleName())
        .hasBindAppId(false)
        .build();
  }

  @Override
  @Transactional
  public void bindAppId(String username, String appId) {
    if (userRepository.findByAppId(appId).isPresent()) {
      throw new BusinessException(ResultCodeEnums.APP_ID_ALREADY_EXISTS);
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException(ResultCodeEnums.USER_NOT_FOUND));

    if (user.getAppId() != null) {
      throw new BusinessException(ResultCodeEnums.FAIL, "用户已绑定小程序");
    }
    user.setAppId(appId);
    userRepository.save(user);
  }
}
