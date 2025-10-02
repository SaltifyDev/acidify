package org.ntqqrev.acidify.internal.packet.message.misc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
open class LightAppPayload(val app: String) {
    companion object {
        val jsonModule = Json {
            ignoreUnknownKeys = true
        }
    }
}