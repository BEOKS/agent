package com.zayden.agent.context.mattermost

import reactor.core.publisher.Mono

interface MattermostAuthentication {
    /**
     * Mattermost 에 로그인하여 인증 토큰을 반환합니다.
     *
     * @param loginCredentials Mattermost 로그인 정보
     * @return 인증 토큰
     */
    fun authenticate(loginCredentials: LoginCredentials): Mono<String>

    /**
     * Spring Property 를 이용해 Mattermost 에 로그인하여 인증 토큰을 반환합니다.
     *
     * @return 인증 토큰
     */
    fun authenticate(): Mono<String>
}
