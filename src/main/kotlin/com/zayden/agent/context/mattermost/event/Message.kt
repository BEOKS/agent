package com.zayden.agent.context.mattermost.event

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Message(
    val channelDisplayName: String,
    val channelName: String,
    val channelType: String,
    val post: Post,
    val senderName: String,
    val setOnline: Boolean,
    val teamId: String?
)
