package com.zayden.agent.context.mattermost.event

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Post(
    val id: String,
    val createAt: Long,
    val updateAt: Long,
    val editAt: Long,
    val deleteAt: Long,
    val isPinned: Boolean,
    val userId: String,
    val channelId: String,
    val rootId: String,
    val originalId: String,
    val message: String,
    val type: String,
    val props: Map<String, Any>,
    val hashtags: String,
    val pendingPostId: String,
    val remoteId: String,
    val replyCount: Int,
    val lastReplyAt: Long,
    val participants: List<String>?,
    val metadata: Map<String, Any>
)
