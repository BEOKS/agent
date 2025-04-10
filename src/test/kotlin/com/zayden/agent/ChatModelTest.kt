package com.zayden.agent

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.StreamingChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.ai.openai.api.ResponseFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class ChatModelTest {

    @Autowired
    private lateinit var chatModel: ChatModel

    @Autowired
    private lateinit var streamingChatModel: StreamingChatModel

    @Test
    fun chat() {
        val call = chatModel.call("hello?")
        assertThat(call).isNotNull
        assertThat(call).contains("Hello! How can I assist you today?")
    }

    @Test
    fun streamingChat() {
        streamingChatModel.stream("hello?").subscribe(
            { response ->
                println("response = $response")
            },
            { error ->
                println("error = $error")
            },
            {
                println("completed")
            }
        )
    }

    /**
     * {
     *   "steps": [
     *     {
     *       "explanation": "Subtract 7 from both sides to isolate the term with x.",
     *       "output": "8x + 7 - 7 = -23 - 7"
     *     },
     *     {
     *       "explanation": "This simplifies to 8x = -30.",
     *       "output": "8x = -30"
     *     },
     *     {
     *       "explanation": "Now, divide both sides by 8 to solve for x.",
     *       "output": "x = -30 / 8"
     *     },
     *     {
     *       "explanation": "Simplify -30 / 8 to get -15 / 4 or -3.75.",
     *       "output": "x = -15/4 or x = -3.75"
     *     }
     *   ],
     *   "final_answer": "x = -15/4 or x = -3.75"
     * }
     */
    @Test
    fun structuredChat() {
        val schema = """
        {
            "type": "object",
            "properties": {
                "steps": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "explanation": { "type": "string" },
                            "output": { "type": "string" }
                        },
                        "required": ["explanation", "output"],
                        "additionalProperties": false
                    }
                },
                "final_answer": { "type": "string" }
            },
            "required": ["steps", "final_answer"],
            "additionalProperties": false
        }
        """

        val prompt = Prompt(
            "how can I solve 8x + 7 = -23",
            OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI)
                .responseFormat(ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, schema))
                .build()
        )
        println("call = ${chatModel.call(prompt).result.output.text}")
    }

}
