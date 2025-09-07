package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

object RegisterInfoResponse : PbSchema() {
    val message = PbString[2]
}