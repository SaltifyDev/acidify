package org.ntqqrev.acidify.pb.dataview

sealed class DataToken(val wireType: Int)

class Varint(val value: Long): DataToken(WireType.VARINT)

class LengthDelimited(val dataBlock: ByteArray): DataToken(WireType.LENGTH_DELIMITED)

class Fixed32(val value: Int) : DataToken(WireType.FIXED32)

class Fixed64(val value: Long) : DataToken(WireType.FIXED64)