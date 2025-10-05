package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object GroupEssenceMessageChange : PbSchema() {
    val groupUin = PbInt64[1]
    val msgSequence = PbInt32[2]
    val random = PbInt32[3]
    val setFlag = PbInt32[4]
    val memberUin = PbInt64[5]
    val operatorUin = PbInt64[6]

    enum class SetFlag(val value: Int) {
        ADD(1),
        REMOVE(2);

        companion object {
            fun from(value: Int) = entries.firstOrNull { it.value == value }
        }
    }
}