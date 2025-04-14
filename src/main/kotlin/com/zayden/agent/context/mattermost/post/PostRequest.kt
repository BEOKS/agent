package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request object for creating a post in Mattermost.
 */
data class PostRequest(
    /**
     * The channel ID to post in
     */
    @JsonProperty("channel_id")
    val channelId: String,

    /**
     * The message contents, can be formatted with Markdown
     */
    val message: String,

    /**
     * The post ID to comment on
     */
    @JsonProperty("root_id")
    val rootId: String? = null,

    /**
     * A list of file IDs to associate with the post (max 5)
     */
    @JsonProperty("file_ids")
    val fileIds: List<String>? = null,

    /**
     * A general JSON property bag to attach to the post
     */
    val props: Map<String, Any>? = null,

    /**
     * A JSON object to add post metadata, e.g., the post's priority
     */
    val metadata: Map<String, Any>? = null
)
