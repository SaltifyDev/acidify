package org.ntqqrev.acidify.pb.util

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source

internal fun Int.varintSize(): Int = when (this) {
    in 0..0x7F -> 1
    in 0x80..0x3FFF -> 2
    in 0x4000..0x1FFFFF -> 3
    in 0x200000..0xFFFFFFF -> 4
    else -> 5
}

internal fun Long.varintSize(): Int = when (this) {
    in 0L..0x7FL -> 1
    in 0x80L..0x3FFFL -> 2
    in 0x4000L..0x1FFFFFL -> 3
    in 0x200000L..0xFFFFFFFL -> 4
    in 0x10000000L..0x7FFFFFFFFL -> 5
    in 0x800000000L..0x3FFFFFFFFFFL -> 6
    in 0x40000000000L..0x1FFFFFFFFFFFFL -> 7
    in 0x2000000000000L..0xFFFFFFFFFFFFFFL -> 8
    in 0x100000000000000L..0x7FFFFFFFFFFFFFFFL -> 9
    else -> 10
}

internal fun Int.encodeVarintToSink(sink: Sink) {
    var value = this
    do {
        var byte = (value and 0x7F)
        value = value ushr 7
        if (value != 0) {
            byte = byte or 0x80
        }
        sink.writeByte(byte.toByte())
    } while (value != 0)
}

internal fun Long.encodeVarintToSink(sink: Sink) {
    var value = this
    do {
        var byte = (value and 0x7F)
        value = value ushr 7
        if (value != 0L) {
            byte = byte or 0x80
        }
        sink.writeByte(byte.toByte())
    } while (value != 0L)
}

internal fun Int.toVarintBuffer(): Buffer {
    val buffer = Buffer()
    encodeVarintToSink(buffer)
    return buffer
}

internal fun Long.toVarintBuffer(): Buffer {
    val buffer = Buffer()
    encodeVarintToSink(buffer)
    return buffer
}

internal fun Source.readVarint32(): Int {
    var result = 0
    var shift = 0
    while (true) {
        val byte = this.readByte().toInt() and 0xFF
        result = result or ((byte and 0x7F) shl shift)
        if (byte and 0x80 == 0) break
        shift += 7
    }
    return result
}

internal fun Source.readVarint64(): Long {
    var result = 0L
    var shift = 0
    while (true) {
        val byte = this.readByte().toLong() and 0xFF
        result = result or ((byte and 0x7F) shl shift)
        if (byte and 0x80 == 0L) break
        shift += 7
    }
    return result
}