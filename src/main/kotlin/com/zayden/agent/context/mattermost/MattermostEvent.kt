
package com.zayden.agent.context.mattermost

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class MattermostEvent(
    val event: String,
    val data: Map<String, Any>,
    val broadcast: Map<String, Any>? = null
) {
}
