package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupReaction : PbSchema() {
    val data = PbOptional[DataOuter[1]]

    internal object DataOuter : PbSchema() {
        val data = PbOptional[DataMiddle[1]]

        internal object DataMiddle : PbSchema() {
            val target = PbOptional[Target[2]]
            val data = PbOptional[DataInner[3]]

            internal object Target : PbSchema() {
                val sequence = PbInt32[1]
            }

            internal object DataInner : PbSchema() {
                val code = PbString[1]
                val count = PbInt32[3]
                val operatorUid = PbString[4]
                val type = PbInt32[5]
            }
        }
    }
}

