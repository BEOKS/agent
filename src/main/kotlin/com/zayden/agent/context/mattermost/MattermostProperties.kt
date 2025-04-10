package com.zayden.agent.context.mattermost

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "agent.context.mattermost")
data class MattermostProperties(
    var host: String = "",
    var loginId: String = "",
    var password: String = "",
    var secretKey: String? = null // 2차 인증 비밀번호 생성을 위한 시크릿키
)
