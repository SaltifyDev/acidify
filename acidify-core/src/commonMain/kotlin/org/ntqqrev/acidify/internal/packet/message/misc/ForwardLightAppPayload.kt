package org.ntqqrev.acidify.internal.packet.message.misc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
class ForwardLightAppPayload(
    val config: JsonObject,
    val desc: String,
    val extra: String,
    val meta: JsonObject,
    val prompt: String,
    val ver: String,
    val view: String,
) : LightAppPayload("com.tencent.multimsg")