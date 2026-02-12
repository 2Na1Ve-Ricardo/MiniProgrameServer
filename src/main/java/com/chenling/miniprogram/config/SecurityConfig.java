package com.chenling.miniprogram.config;

import com.chenling.miniprogram.common.Response.Results;
import com.chenling.miniprogram.common.enums.ResultCodeEnums;
import com.chenling.miniprogram.security.components.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.PageAttributes.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final ObjectMapper objectMapper;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.objectMapper = objectMapper;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/api/auth/**").permitAll()
        .antMatchers("/api/static/**").permitAll()
        .antMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
        .antMatchers("/api/super/**").hasRole("SUPER_ADMIN")
        .anyRequest().authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEnterPoint())
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * 配置用户为登陆或登陆过期问题
   */
  @Bean
  public AuthenticationEntryPoint authenticationEnterPoint() {
    return ((request, response, authException) -> {
      response.setContentType("application/json");
      response.setStatus(401);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(objectMapper.writeValueAsString(Results.fail(
          ResultCodeEnums.UNAUTHORIZED)));
    });
  }


  /**
   * 配置无权限访问处理
   */
  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return ((request, response, accessDeniedException) -> {
      response.setContentType("application/json");
      response.setStatus(403);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(objectMapper.writeValueAsString(Results.fail(
          ResultCodeEnums.FORBIDDEN)));
    });
  }
}
