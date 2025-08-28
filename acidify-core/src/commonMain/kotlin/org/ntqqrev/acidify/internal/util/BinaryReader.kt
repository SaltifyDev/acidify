package org.ntqqrev.acidify.internal.util

internal class BinaryReader(val bytes: ByteArray) {
    private var position = 0

    val current: Int
        get() = position
    val remaining: Int
        get() = bytes.size - position

    fun readByte(): Byte {
        return bytes[position++]
    }

    fun readUByte(): UByte {
        return bytes[position++].toUByte()
    }

    fun readShort(): Short {
        return (bytes[position++].toInt() shl 8 or
                (bytes[position++].toInt() and 0xff)).toShort()
    }

    fun readUShort(): UShort {
        return (bytes[position++].toInt() shl 8 or
                (bytes[position++].toInt() and 0xff)).toUShort()
    }

    fun readInt(): Int {
        return (bytes[position++].toInt() shl 24 or
                (bytes[position++].toInt() and 0xff shl 16) or
                (bytes[position++].toInt() and 0xff shl 8) or
                (bytes[position++].toInt() and 0xff))
    }

    fun readUInt(): UInt {
        return readInt().toUInt()
    }

    fun readLong(): Long {
        return (bytes[position++].toLong() shl 56 or
                (bytes[position++].toLong() and 0xff shl 48) or
                (bytes[position++].toLong() and 0xff shl 40) or
                (bytes[position++].toLong() and 0xff shl 32) or
                (bytes[position++].toLong() and 0xff shl 24) or
                (bytes[position++].toLong() and 0xff shl 16) or
                (bytes[position++].toLong() and 0xff shl 8) or
                (bytes[position++].toLong() and 0xff))
    }

    fun readULong(): ULong {
        return readLong().toULong()
    }

    fun readBytes(length: Int): ByteArray {
        return bytes.copyOfRange(position, position + length).also {
            position += length
        }
    }

    fun readBytes() = readBytes(remaining)

    fun readByteArray(length: Int): ByteArray {
        return readBytes(length)
    }

    fun readByteArray() = readBytes(remaining)

    fun readString(length: Int): String {
        return readBytes(length).decodeToString()
    }

    fun skip(length: Int) {
        position += length
    }

    fun discard(length: Int) {
        position += length
    }

    private fun readLength(prefix: Prefix): UInt {
        val prefixLength = prefix.getPrefixLength()
        val includePrefix = prefix.isIncludePrefix()

        return when (prefixLength) {
            1 -> this.readByte().toUInt() - (if (includePrefix) prefixLength else 0).toUInt()
            2 -> this.readUShort().toUInt() - (if (includePrefix) prefixLength else 0).toUInt()
            4 -> this.readUInt() - (if (includePrefix) prefixLength else 0).toUInt()
            else -> 0u
        }
    }

    fun readPrefixedBytes(prefix: Prefix): ByteArray {
        val length = readLength(prefix)
        return readBytes(length.toInt())
    }

    fun readPrefixedString(prefix: Prefix): String {
        val length = readLength(prefix)
        return readBytes(length.toInt()).decodeToString()
    }
}

internal open class Prefix(val value: Int) {
    data object NONE : Prefix(0b0000)

    data object UINT_8 : Prefix(0b0010)

    data object UINT_16 : Prefix(0b0100)

    data object UINT_32 : Prefix(0b1000)

    data object INCLUDE_PREFIX : Prefix(0b0001)

    data object LENGTH_ONLY : Prefix(0b0000)

    companion object {
        fun values(): Array<Prefix> = arrayOf(NONE, UINT_8, UINT_16, UINT_32, INCLUDE_PREFIX, LENGTH_ONLY)

        fun valueOf(value: String): Prefix = when (value) {
            "NONE" -> NONE
            "UINT_8" -> UINT_8
            "UINT_16" -> UINT_16
            "UINT_32" -> UINT_32
            "INCLUDE_PREFIX" -> INCLUDE_PREFIX
            "LENGTH_ONLY" -> LENGTH_ONLY
            else -> throw IllegalArgumentException("No object org.lagrange.dev.utils.ext.Prefix.$value")
        }
    }

    fun getPrefixLength(): Int = (this.value and 0b1110) shr 1

    fun isIncludePrefix(): Boolean = (this.value and 0b0001) == 1

    infix fun or(other: Prefix): Prefix = Prefix(this.value or other.value)
}