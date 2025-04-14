package com.zayden.agent.context.mattermost

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest
class MattermostPosterTest {

    @Autowired
    private lateinit var mattermostPoster: MattermostPoster

    private val channelId = "5y4rp6rtjjgo7pi181di137r8w"

    @Test
    fun `test post message to channel`() {
        // Given
        val message = "Integration test message at ${System.currentTimeMillis()}"

        // When
        val result = mattermostPoster.createPost(channelId, message)

        // Then
        StepVerifier.create(result)
            .expectNextMatches { response ->
                response.id.isNotEmpty() &&
                        response.channelId == channelId &&
                        response.message == message
            }
            .expectComplete()
            .verify(Duration.ofSeconds(10))
    }
}
