package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object Oidb : PbSchema() {
    val command = PbInt32[1]
    val service = PbInt32[2]
    val result = PbInt32[3]
    val body = PbBytes[4]
    val message = PbString[5]
    val reserved = PbBoolean[12]
}