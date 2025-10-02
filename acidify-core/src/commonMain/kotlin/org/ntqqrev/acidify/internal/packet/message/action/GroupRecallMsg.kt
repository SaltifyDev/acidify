package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.pb.*

internal object GroupRecallMsg : PbSchema() {
    val type = PbInt32[1]
    val groupUin = PbInt64[2]
    val info = PbOptional[Info[3]]
    val field4 = PbOptional[Field4[4]]

    internal object Info : PbSchema() {
        val sequence = PbInt64[1]
        val random = PbInt32[2]
        val field3 = PbInt32[3]
    }

    internal object Field4 : PbSchema() {
        val field1 = PbInt32[1]
    }
}

