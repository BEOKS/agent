package com.zayden.agent.context.mattermost

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MattermostAuthenticationTest {

    @Autowired
    private lateinit var mattermostAuthentication: MattermostAuthentication

    @Test
    fun test(){
        val token = mattermostAuthentication.authenticate().block()
        assertThat(token).isNotNull
        assertThat(token).isNotEmpty
        println("Token = $token")
    }

}
