package com.chenling.miniprogram.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeChatLoginDTO {

    private String openid;

    @JsonProperty("session_key")
    private String sessionKey;

    private String unionid;

    @JsonProperty("errcode")
    private String errCode;

    @JsonProperty("errmsg")
    private String errMsg;

}
