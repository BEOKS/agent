package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

/**
 * Response object returned after creating a post in Mattermost.
 */
data class PostResponse(
    /**
     * The unique identifier for the post
     */
    val id: String,

    /**
     * The time the post was created
     */
    @JsonProperty("create_at")
    val createAt: Long? = null,

    /**
     * The time the post was last updated
     */
    @JsonProperty("update_at")
    val updateAt: Long? = null,

    /**
     * The time the post was deleted, if applicable
     */
    @JsonProperty("delete_at")
    val deleteAt: Long? = null,

    /**
     * The channel ID the post was created in
     */
    @JsonProperty("channel_id")
    val channelId: String,

    /**
     * The ID of the user who created the post
     */
    @JsonProperty("user_id")
    val userId: String,

    /**
     * The message content of the post
     */
    val message: String,

    /**
     * The type of post (usually empty string for normal posts)
     */
    val type: String? = null,

    /**
     * The post ID this post is a comment on, if applicable
     */
    @JsonProperty("root_id")
    val rootId: String? = null,

    /**
     * The parent post ID, if applicable
     */
    @JsonProperty("parent_id")
    val parentId: String? = null,

    /**
     * The original post ID if this post has been edited, if applicable
     */
    @JsonProperty("original_id")
    val originalId: String? = null,

    /**
     * A list of file IDs associated with the post
     */
    @JsonProperty("file_ids")
    val fileIds: List<String>? = null,

    /**
     * Custom properties attached to the post
     */
    val props: Map<String, Any>? = null,

    /**
     * Metadata attached to the post
     */
    val metadata: Map<String, Any>? = null,

    /**
     * Whether the post has been pinned to the channel
     */
    @JsonProperty("is_pinned")
    val isPinned: Boolean? = null
) {
    /**
     * Get create time as Instant
     */
    val createdAtInstant: Instant?
        get() = createAt?.let { Instant.ofEpochMilli(it) }

    /**
     * Get update time as Instant
     */
    val updatedAtInstant: Instant?
        get() = updateAt?.let { Instant.ofEpochMilli(it) }

    /**
     * Get delete time as Instant, only if actually deleted
     */
    val deletedAtInstant: Instant?
        get() = if (deleteAt != null && deleteAt > 0) Instant.ofEpochMilli(deleteAt) else null
}
