package org.ntqqrev.acidify.event.internal

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.AcidifyEvent

internal abstract class Signal(val cmd: String) {
    abstract fun parse(bot: Bot, payload: ByteArray): List<AcidifyEvent>
}