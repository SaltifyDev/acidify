package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupInvitedJoinRequest : PbSchema() {
    val command = PbInt32[1]
    val info = PbOptional[Info[2]]

    internal object Info : PbSchema() {
        val inner = PbOptional[Inner[1]]

        internal object Inner : PbSchema() {
            val groupUin = PbInt64[1]
            val targetUid = PbString[5]
            val invitorUid = PbString[6]
        }
    }
}

