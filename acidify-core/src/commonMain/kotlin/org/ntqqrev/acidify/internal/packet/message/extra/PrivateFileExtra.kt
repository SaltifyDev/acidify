package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.internal.packet.message.NotOnlineFile
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object PrivateFileExtra : PbSchema() {
    val notOnlineFile = NotOnlineFile[1]
}