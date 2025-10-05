package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupMute : PbSchema() {
    val groupUin = PbInt64[1]
    val operatorUid = PbString[4]
    val info = PbOptional[Info[5]]

    internal object Info : PbSchema() {
        val timestamp = PbInt32[1]
        val state = PbOptional[State[3]]

        internal object State : PbSchema() {
            val targetUid = PbOptional[PbString[1]]
            val duration = PbInt32[2]
        }
    }
}

