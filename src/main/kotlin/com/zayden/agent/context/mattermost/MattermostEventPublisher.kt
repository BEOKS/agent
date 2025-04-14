package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValues
import com.zayden.agent.logger
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Mono
import java.net.URI

private const val WEBSOCKET_PATH = "/api/v4/websocket"

@Service
class MattermostEventPublisher(
    private val mattermostAuthentication: MattermostAuthentication,
    private val mattermostProperties: MattermostProperties,
    private val subscriber: MutableList<Subscriber<in MattermostEvent>>
): Publisher<MattermostEvent> {
    val log = logger()
    val objectMapper = jacksonObjectMapper()

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
                        .thenMany(
                            session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .map { message: String ->
                                    subscriber.forEach { it.onNext(convertToMattermostEvent(message)) } })
                        .then()
                }
            }
            .doOnError { error ->
                log.error("WebSocket connection error", error)
            }
    }

    private fun convertToMattermostEvent(message: String): MattermostEvent {
        return objectMapper.readValue(message, MattermostEvent::class.java)
    }

    override fun subscribe(s: Subscriber<in MattermostEvent>?) {
        if (s == null) {
            throw NullPointerException("Subscriber cannot be null")
        }
        subscriber.add(s)
    }
}
