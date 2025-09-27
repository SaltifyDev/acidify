package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object CommonMessage : PbSchema() {
    val routingHead = RoutingHead[1]
    val contentHead = ContentHead[2]
    val messageBody = MessageBody[3]
}