package org.ntqqrev.acidify.event.internal

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.AcidifyEvent

internal abstract class AbstractSignal(val cmd: String) {
    abstract suspend fun parse(bot: Bot, payload: ByteArray): List<AcidifyEvent>
}