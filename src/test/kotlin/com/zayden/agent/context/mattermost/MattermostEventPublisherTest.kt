package com.zayden.agent.context.mattermost

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class MattermostEventPublisherTest {
    @Autowired
    private lateinit var mattermostEventPublisher: MattermostEventPublisher

    @Test
    fun test() {
        mattermostEventPublisher.connect().block(Duration.ofMinutes(10))
    }
}
