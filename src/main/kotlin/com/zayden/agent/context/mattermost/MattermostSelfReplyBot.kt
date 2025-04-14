package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zayden.agent.context.mattermost.event.Message
import com.zayden.agent.context.mattermost.event.Post
import com.zayden.agent.logger
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.stereotype.Component

@Component
class MattermostSelfReplyBot(
    private val mattermostPoster: MattermostPoster,
) : Subscriber<MattermostEvent> {
    val HEADER = "> Generate By Bot"
    val TARGET_CHANNEL_ID = "5y4rp6rtjjgo7pi181di137r8w"
    val log = logger()
    val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun onSubscribe(s: Subscription?) {
        log.info("MattermostSelfReplyBot onSubscribe: $s")
        s?.request(Long.MAX_VALUE) // Request unlimited elements
    }

    override fun onError(t: Throwable?) {
        log.error("MattermostSelfReplyBot onError: $t")
    }

    override fun onComplete() {
        log.info("MattermostSelfReplyBot onComplete")
    }

    override fun onNext(mattermostEvent: MattermostEvent?) {
        log.trace("MattermostSelfReplyBot onNext: {}", mattermostEvent)
        val message = convert2Message(mattermostEvent) ?: return
        
        mattermostPoster.createPost(
            PostRequest(
                channelId = TARGET_CHANNEL_ID,
                message = "$HEADER\n${message.post.message}",
            )
        ).subscribe(
            { createdPost -> log.info("Post created successfully: $createdPost") },
            { error -> log.error("Error creating post: $error") }
        )
    }

    private fun convert2Message(mattermostEvent: MattermostEvent?): Message? {
        if (mattermostEvent == null) {
            log.trace("MattermostSelfReplyBot onNext: null")
            return null
        }
        if (mattermostEvent.event != "posted") {
            log.trace("MattermostSelfReplyBot onNext: not posted")
            return null
        }

        try {
            // First convert data to a JsonNode
            val dataNode = objectMapper.valueToTree<JsonNode>(mattermostEvent.data)

            // The post is a nested JSON string that needs special handling
            val postStr = dataNode.get("post")?.asText()
            val message = if (postStr != null) {
                // Parse the post string to a PostData object
                val postData = objectMapper.readValue<Post>(postStr)

                // Create a Message with all the data
                Message(
                    channelDisplayName = dataNode.get("channel_display_name")?.asText() ?: "",
                    channelName = dataNode.get("channel_name")?.asText() ?: "",
                    channelType = dataNode.get("channel_type")?.asText() ?: "",
                    post = postData,
                    senderName = dataNode.get("sender_name")?.asText() ?: "",
                    setOnline = dataNode.get("set_online")?.asBoolean() ?: false,
                    teamId = dataNode.get("team_id")?.asText() ?: ""
                )
            } else {
                // If post is null, try to read the message directly (fallback)
                objectMapper.treeToValue(dataNode, Message::class.java)
            }

            if (message.post.message.startsWith(HEADER)) {
                log.trace("MattermostSelfReplyBot onNext: bot message, ignoring")
                return null
            }
            return message
        } catch (e: Exception) {
            log.error("Error converting to Message: ${e.message}", e)
            return null
        }
    }
}
