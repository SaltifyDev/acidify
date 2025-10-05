package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupMemberChange : PbSchema() {
    val groupUin = PbInt64[1]
    val memberUid = PbString[3]
    val type = PbInt32[4]
    val operatorInfo = PbOptional[PbBytes[5]]

    internal object OperatorInfo : PbSchema() {
        val body = PbOptional[Body[1]]

        internal object Body : PbSchema() {
            val uid = PbString[1]
        }
    }

    enum class IncreaseType(val value: Int) {
        APPROVE(130),
        INVITE(131);

        companion object {
            fun from(value: Int) = entries.firstOrNull { it.value == value }
        }
    }

    enum class DecreaseType(val value: Int) {
        KICK_SELF(3),
        EXIT(130),
        KICK(131);

        companion object {
            fun from(value: Int) = entries.firstOrNull { it.value == value }
        }
    }
}

