package com.zayden.agent.context.mattermost.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MattermostUserServiceTest {
    @Autowired
    private lateinit var mattermostUserService: MattermostUserService

    @Test
    fun me(){
        val user = mattermostUserService.me().block()
        println("user = $user")
    }
}
