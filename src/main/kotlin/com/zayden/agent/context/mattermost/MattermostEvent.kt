package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// Make sure your MattermostEvent class looks like this:
@JsonIgnoreProperties(ignoreUnknown = true)
data class MattermostEvent(
    val event: String = "",
    val data: Map<String, Any> = mapOf(),
    val broadcast: Map<String, Any>? = null,
    val seq: Int = 0
) {
    // Default constructor required for Jackson deserialization from JSON string
    constructor() : this("", mapOf(), null, 0)
}
