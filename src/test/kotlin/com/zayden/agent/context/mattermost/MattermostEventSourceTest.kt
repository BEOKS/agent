package com.zayden.agent.context.mattermost

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class MattermostEventSourceTest {
    @Autowired
    private lateinit var mattermostEventSource: MattermostEventSource

    @Test
    fun test() {
        mattermostEventSource.connect().block(Duration.ofMinutes(10))
    }
}
