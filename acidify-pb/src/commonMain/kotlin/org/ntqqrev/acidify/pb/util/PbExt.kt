package org.ntqqrev.acidify.pb.util

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.ntqqrev.acidify.pb.dataview.*

internal fun Buffer.readTokens(): MultiMap<Int, DataToken> {
    val result = multiMapOf<Int, DataToken>()
    while (!this.exhausted()) {
        val (fieldNumber, wireType) = readTag()
        result.put(
            fieldNumber,
            when (wireType) {
                WireType.VARINT -> Varint(readVarint64())

                WireType.LENGTH_DELIMITED -> {
                    val length = readVarint32()
                    val byteArray = readByteArray(length)
                    LengthDelimited(byteArray)
                }

                WireType.FIXED32 -> Fixed32(readInt())

                WireType.FIXED64 -> Fixed64(readLong())

                else -> throw IllegalArgumentException("Unsupported wire type: $wireType")
            }
        )
    }
    return result
}

internal fun MultiMap<Int, DataToken>.encodeToBuffer(): Buffer {
    val buffer = Buffer()
    this.forEach { (fieldNumber, tokenList) ->
        tokenList.forEach { token ->
            val key = (fieldNumber shl 3) or token.wireType
            key.encodeVarintToSink(buffer)
            when (token) {
                is Varint -> token.value.encodeVarintToSink(buffer)
                is LengthDelimited -> {
                    token.dataBlock.size.encodeVarintToSink(buffer)
                    buffer.write(token.dataBlock)
                }
                is Fixed32 -> buffer.writeInt(token.value)
                is Fixed64 -> buffer.writeLong(token.value)
            }
        }
    }
    return buffer
}

internal fun Buffer.readTag(): Pair<Int, Int> {
    val key = readVarint32()
    val fieldNumber = key shr 3
    val wireType = key and 0x07
    return Pair(fieldNumber, wireType)
}