package com.chenling.miniprogram.security.controller;

import com.chenling.miniprogram.common.Response.Results;
import com.chenling.miniprogram.security.dto.LoginRequest;
import com.chenling.miniprogram.security.dto.RegistryRequest;
import com.chenling.miniprogram.security.service.service_interface.IAuthenticationService;
import com.chenling.miniprogram.security.vo.LoginVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户登录相关接口
 */

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Resource
    IAuthenticationService authenticationService;

    @GetMapping("/{jsCode}")
    public Results<String> getWeChatOpenId(@PathVariable String jsCode) {
        String openId = authenticationService.getWechatOpenid(jsCode);
        return Results.success(openId);
    }

    @PostMapping("/register")
    public Results<Boolean> register(@RequestBody RegistryRequest registryRequest){
        boolean result = authenticationService.register(registryRequest);
        return Results.success(result);
    }

    @PostMapping("/login")
    public Results<LoginVo> login(@RequestBody LoginRequest request){
        LoginVo loginVo = authenticationService.login(request);
        return Results.success(loginVo);
    }

    @PostMapping("/token/validate")
    public Results<Boolean> isTokenValid(@RequestBody String token){
        return Results.success(authenticationService.isTokenValid(token));
    }
}
