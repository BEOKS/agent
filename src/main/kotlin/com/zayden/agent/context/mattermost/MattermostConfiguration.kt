package com.zayden.agent.context.mattermost

import com.zayden.agent.logger
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit


@Configuration
class MattermostConfiguration {
    val log = logger()

    @Bean
    fun mattermostProperties(): MattermostProperties {
        val mattermostProperties = MattermostProperties()
        log.info("Mattermost 설정 정보: $mattermostProperties")
        return mattermostProperties
    }

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            }
    }

    @Bean
    @Qualifier("basicWebClient")
    fun mattermostWebClient(mattermostProperties: MattermostProperties, httpClient: HttpClient): WebClient {
        return WebClient.builder()
            .baseUrl(mattermostProperties.host)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

    @Bean
    @Qualifier("authWebClient")
    fun mattermostAuthWebClient(
        mattermostAuthentication: MattermostAuthentication,
        mattermostProperties: MattermostProperties,
        httpClient: HttpClient
    ): WebClient {
        return mattermostAuthentication.authenticate()
            .map { token ->
                WebClient.builder()
                    .baseUrl(mattermostProperties.host)
                    .clientConnector(ReactorClientHttpConnector(httpClient))
                    .defaultHeaders { headers ->
                        headers.set("Authorization", "Bearer $token")
                    }.build()
            }.block() ?: throw IllegalStateException("Failed to authenticate with Mattermost")
    }
}
