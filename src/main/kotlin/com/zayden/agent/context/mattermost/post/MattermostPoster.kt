package com.zayden.agent.context.mattermost

import com.zayden.agent.logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * Class for posting messages to Mattermost channels using WebFlux WebClient.
 */
@Component
class MattermostPoster(@Qualifier("authWebClient") private val webClient: WebClient) {
    private val log = logger()

    /**
     * Creates a simple post in a channel.
     *
     * @param channelId the channel ID to post in
     * @param message the message content (supports Markdown)
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createPost(channelId: String, message: String, setOnline: Boolean = true): Mono<PostResponse> {
        return createPost(
            PostRequest(
                channelId = channelId,
                message = message
            ),
            setOnline
        )
    }

    /**
     * Creates a post as a comment to another post.
     *
     * @param channelId the channel ID to post in
     * @param message the message content (supports Markdown)
     * @param rootId the post ID to comment on
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createComment(channelId: String, message: String, rootId: String, setOnline: Boolean = true): Mono<PostResponse> {
        return createPost(
            PostRequest(
                channelId = channelId,
                message = message,
                rootId = rootId
            ),
            setOnline
        )
    }

    /**
     * Creates a post with attached files.
     *
     * @param channelId the channel ID to post in
     * @param message the message content (supports Markdown)
     * @param fileIds list of file IDs to associate with the post (max 5)
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createPostWithFiles(
        channelId: String,
        message: String,
        fileIds: List<String>,
        setOnline: Boolean = true
    ): Mono<PostResponse> {
        if (fileIds.size > 5) {
            return Mono.error(IllegalArgumentException("Posts are limited to 5 files maximum"))
        }

        return createPost(
            PostRequest(
                channelId = channelId,
                message = message,
                fileIds = fileIds
            ),
            setOnline
        )
    }

    /**
     * Creates a post with custom properties.
     *
     * @param channelId the channel ID to post in
     * @param message the message content (supports Markdown)
     * @param props custom properties to attach to the post
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createPostWithProps(
        channelId: String,
        message: String,
        props: Map<String, Any>,
        setOnline: Boolean = true
    ): Mono<PostResponse> {
        return createPost(
            PostRequest(
                channelId = channelId,
                message = message,
                props = props
            ),
            setOnline
        )
    }

    /**
     * Creates a post with metadata.
     *
     * @param channelId the channel ID to post in
     * @param message the message content (supports Markdown)
     * @param metadata metadata to attach to the post
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createPostWithMetadata(
        channelId: String,
        message: String,
        metadata: Map<String, Any>,
        setOnline: Boolean = true
    ): Mono<PostResponse> {
        return createPost(
            PostRequest(
                channelId = channelId,
                message = message,
                metadata = metadata
            ),
            setOnline
        )
    }

    /**
     * Creates a post in a channel using a fully customized request.
     *
     * @param postRequest the post request object
     * @param setOnline whether to set the user status as online
     * @return Mono containing the created post response
     */
    fun createPost(postRequest: PostRequest, setOnline: Boolean = true): Mono<PostResponse> {
        log.debug("Creating post in channel: ${postRequest.channelId}")

        return webClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v4/posts")
                    .queryParam("set_online", setOnline)
                    .build()
            }
            .bodyValue(postRequest)
            .retrieve()
            .bodyToMono(PostResponse::class.java)
            .doOnSuccess { response -> log.debug("Successfully created post with ID: ${response.id}") }
            .doOnError { error -> log.error("Failed to create post: ${error.message}") }
    }
}
