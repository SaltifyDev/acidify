package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object FriendRecall : PbSchema() {
    val body = PbOptional[Body[1]]

    internal object Body : PbSchema() {
        val fromUid = PbString[1]
        val toUid = PbString[2]
        val clientSequence = PbInt32[3]
        val tipInfo = PbOptional[TipInfo[13]]
        val sequence = PbInt32[20]

        internal object TipInfo : PbSchema() {
            val tip = PbString[2]
        }
    }
}