package com.chenling.miniprogram.security.service;

import com.chenling.miniprogram.security.entity.User;
import com.chenling.miniprogram.security.mapper.IUserRepository;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

  private final IUserRepository userRepository;

  public CustomUserDetailService(IUserRepository userRepository) {
    this.userRepository = userRepository;
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("用户名不存在: " + username ));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleCode()))
    );
  }

  public User loadUserByAppId(String appId) {
    return userRepository.findByAppId(appId)
            .orElse(null);
  }
}
