package org.ntqqrev.acidify.pb.dataview

internal sealed class DataToken(val wireType: Int)

internal class Varint(val value: Long): DataToken(WireType.VARINT)

internal class LengthDelimited(val dataBlock: ByteArray): DataToken(WireType.LENGTH_DELIMITED)