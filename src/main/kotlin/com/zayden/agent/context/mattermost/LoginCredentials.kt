package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginCredentials(
    /**
     * 메타모스트 로그인 아이디
     */
    @JsonProperty("login_id")
    val loginId: String,

    /**
     * 메타모스트 로그인 비밀번호
     */
    @JsonProperty("password")
    val password: String,

    /**
     * 2차 인증 활성화 시 사용하는 6자리 비밀번호
     * 사용하지 않는 경우 Null 사용
     */
    val token: String? = null,
)
