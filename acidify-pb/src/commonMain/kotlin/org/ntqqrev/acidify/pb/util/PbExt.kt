package org.ntqqrev.acidify.pb.util

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.ntqqrev.acidify.pb.dataview.DataToken
import org.ntqqrev.acidify.pb.dataview.LengthDelimited
import org.ntqqrev.acidify.pb.dataview.Varint
import org.ntqqrev.acidify.pb.dataview.WireType

internal fun Buffer.readTokens(): MultiMap<Int, DataToken> {
    val result = multiMapOf<Int, DataToken>()
    while (!this.exhausted()) {
        val (fieldNumber, wireType) = readTag()
        val token = when (wireType) {
            WireType.VARINT -> Varint(readVarint64())

            WireType.LENGTH_DELIMITED -> {
                val length = readVarint32()
                val byteArray = readByteArray(length)
                LengthDelimited(byteArray)
            }

            WireType.FIXED32 -> {
                skip(4)
                null
            }

            WireType.FIXED64 -> {
                skip(8)
                null
            }

            else -> throw IllegalArgumentException("Unsupported wire type: $wireType")
        }
        if (token != null) {
            result.put(fieldNumber, token)
        }
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