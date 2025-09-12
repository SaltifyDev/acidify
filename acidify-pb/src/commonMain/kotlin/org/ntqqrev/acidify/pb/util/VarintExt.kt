package org.ntqqrev.acidify.pb.util

import kotlinx.io.Sink
import kotlinx.io.Source

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