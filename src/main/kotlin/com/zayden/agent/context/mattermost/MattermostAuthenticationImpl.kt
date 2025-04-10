package com.zayden.agent.context.mattermost

import com.zayden.agent.util.TotpGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private const val TOKEN_HEADER_NAME = "Token"
private const val CANNOT_FIND_TOKEN_MSG = "토큰을 찾을 수 없습니다."
private const val LOGIN_PATH = "/api/v4/users/login"

@Service
class MattermostAuthenticationImpl(
    private val mattermostProperties: MattermostProperties,
    private val totpGenerator: TotpGenerator,
    @Qualifier("basicWebClient") private val mattermostWebClient: WebClient
) : MattermostAuthentication {
    override fun authenticate(loginCredentials: LoginCredentials): Mono<String> {
        return mattermostWebClient
            .post()
            .uri(LOGIN_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginCredentials)
            .retrieve()
            .toBodilessEntity()
            .handle{ response, sink ->
                val token = response.headers[TOKEN_HEADER_NAME]?.get(0) ?: ""
                if (token.isEmpty()) {
                    sink.error(IllegalStateException(CANNOT_FIND_TOKEN_MSG))
                }
                else{
                    sink.next(token)
                }
            }
    }

    /**
     * Spring Property 를 이용해 Mattermost 에 로그인하여 인증 토큰을 반환합니다.
     *
     * @return 인증 토큰
     */
    override fun authenticate(): Mono<String> {
        val loginCredentials = LoginCredentials(
            loginId = mattermostProperties.loginId,
            password = mattermostProperties.password,
            token = mattermostProperties.secretKey?.let { totpGenerator.generate(it) }
        )

        return authenticate(loginCredentials)
    }
}
