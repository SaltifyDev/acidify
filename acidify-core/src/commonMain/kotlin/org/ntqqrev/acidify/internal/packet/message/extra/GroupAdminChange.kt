package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupAdminChange : PbSchema() {
    val groupUin = PbInt64[1]
    val body = PbOptional[Body[4]]

    internal object Body : PbSchema() {
        val unset = PbOptional[TargetInfo[1]]
        val set = PbOptional[TargetInfo[2]]

        internal object TargetInfo : PbSchema() {
            val targetUid = PbString[1]
        }
    }
}

