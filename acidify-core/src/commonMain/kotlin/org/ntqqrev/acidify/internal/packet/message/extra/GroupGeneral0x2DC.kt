package org.ntqqrev.acidify.internal.packet.message.extra

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readUInt
import org.ntqqrev.acidify.internal.util.writeBytes
import org.ntqqrev.acidify.pb.*

internal class GroupGeneral0x2DC {
    val groupUin: Long
    val body: ByteArray

    constructor(byteArray: ByteArray) {
        val buffer = Buffer()
        buffer.writeBytes(byteArray)
        this.groupUin = buffer.readUInt().toLong()
        buffer.readByte()
        buffer.readShort()
        body = buffer.readByteArray()
    }

    internal object Body : PbSchema() {
        val type = PbInt32[1]
        val groupUin = PbInt64[4]
        val eventParam = PbOptional[PbBytes[5]]
        val recall = PbOptional[GroupRecall[11]]
        val field13 = PbOptional[PbInt32[13]]
        val operatorUid = PbOptional[PbString[21]]
        val generalGrayTip = PbOptional[GeneralGrayTip[26]]
        val essenceMessageChange = PbOptional[GroupEssenceMessageChange[33]]
        val msgSequence = PbInt32[37]
        val reaction = PbOptional[GroupReaction[44]]
    }
}