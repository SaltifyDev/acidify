package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchGroupExtraInfoReq : PbSchema() {
    object Config : PbSchema() {
        object Flags : PbSchema() {
            val latestMessageSeq = PbBoolean[22]
        }

        val groupUin = PbInt64[1]
        val flags = Flags[2]
    }

    val random = PbInt32[1]
    val config = Config[2]
}

internal object FetchGroupExtraInfoResp : PbSchema() {
    object Info : PbSchema() {
        object Results : PbSchema() {
            val latestMessageSeq = PbInt64[22]
        }

        val groupUin = PbInt64[1]
        val results = Results[3]
    }

    val info = Info[1]
}

