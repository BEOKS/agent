package com.zayden.agent.context.mattermost

import com.zayden.agent.logger
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

private const val WEBSOCKET_PATH = "/api/v4/websocket"

@Service
class MattermostEventSource(
    private val mattermostAuthentication: MattermostAuthentication,
    private val mattermostProperties: MattermostProperties
) {
    val log = logger()

    fun connect(): Mono<Void> {
        val headers = HttpHeaders()

        return mattermostAuthentication.authenticate()
            .flatMap { token ->
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer $token")
                val client: WebSocketClient = ReactorNettyWebSocketClient()

                val wsUrl = mattermostProperties.host.replace("https://", "wss://") + WEBSOCKET_PATH
                log.info("Connecting to WebSocket at $wsUrl")

                client.execute(
                    URI.create(wsUrl),
                    headers
                ) { session ->
                    val authMessage = "{ \"seq\": 1, \"action\": \"authentication_challenge\", \"data\": { \"token\": \"$token\" } }"
                    session.send(Mono.just(session.textMessage(authMessage)))
                        .thenMany(session.receive().map(WebSocketMessage::getPayloadAsText).log())
                        .then()
                }
            }
            .doOnError { error ->
                log.error("WebSocket connection error", error)
            }
    }
}
