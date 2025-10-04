package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object BroadcastGroupFileReq : PbSchema() {
    val body = Body[5]

    object Body : PbSchema() {
        val groupUin = PbInt64[1]
        val type = PbInt32[2]
        val info = Info[3]

        internal object Info : PbSchema() {
            val busiType = PbInt32[1]
            val fileId = PbString[2]
            val field3 = PbInt32[3]
            val field4 = PbString[4]
            val field5 = PbBoolean[5]
        }
    }
}