package com.zayden.agent

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!


@EnableConfigurationProperties
@SpringBootApplication
class AgentApplication

fun main(args: Array<String>) {
    runApplication<AgentApplication>(*args)
}
