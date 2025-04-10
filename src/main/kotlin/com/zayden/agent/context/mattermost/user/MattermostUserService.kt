package com.zayden.agent.context.mattermost.user

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class MattermostUserService(
    @Qualifier("authWebClient") private val mattermostAuthWebClient: WebClient,
) {
    fun me(): Mono<String>{
        return mattermostAuthWebClient
            .get()
            .uri("/api/v4/users/me")
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
