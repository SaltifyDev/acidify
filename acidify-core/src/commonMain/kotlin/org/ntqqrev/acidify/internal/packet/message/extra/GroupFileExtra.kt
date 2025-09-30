package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupFileExtra : PbSchema() {
    val field1 = PbInt32[1]
    val fileName = PbString[2]
    val display = PbString[3]
    val inner = Inner[7]

    internal object Inner : PbSchema() {
        val info = Info[2]

        internal object Info : PbSchema() {
            val busId = PbInt32[1]
            val fileId = PbString[2]
            val fileSize = PbInt64[3]
            val fileName = PbString[4]
            val field5 = PbInt32[5]
            val field7 = PbString[7]
            val fileMd5 = PbString[8]
        }
    }
}