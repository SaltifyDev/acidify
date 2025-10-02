package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.pb.*

internal object C2CRecallMsg : PbSchema() {
    val type = PbInt32[1]
    val targetUid = PbString[3]
    val info = PbOptional[Info[4]]
    val settings = PbOptional[Settings[5]]
    val field6 = PbBoolean[6]

    internal object Info : PbSchema() {
        val clientSequence = PbInt64[1]
        val random = PbInt32[2]
        val messageId = PbInt64[3]
        val timestamp = PbInt64[4]
        val field5 = PbInt32[5]
        val messageSequence = PbInt64[6]
    }

    internal object Settings : PbSchema() {
        val field1 = PbBoolean[1]
        val field2 = PbBoolean[2]
    }
}