package org.ntqqrev.acidify.pb

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.ntqqrev.acidify.pb.dataview.DataToken
import org.ntqqrev.acidify.pb.dataview.LengthDelimited
import org.ntqqrev.acidify.pb.dataview.Varint
import org.ntqqrev.acidify.pb.util.encodeVarintToSink
import org.ntqqrev.acidify.pb.util.readVarint32
import org.ntqqrev.acidify.pb.util.encodeToBuffer
import org.ntqqrev.acidify.pb.util.readVarint64

abstract class PbType<T>(val fieldNumber: Int) {
    internal abstract fun encode(value: T): MutableList<DataToken>
    internal abstract fun decode(tokens: List<DataToken>): T
    internal abstract val defaultValue: T
}

class PbInt32(fieldNumber: Int) : PbType<Int>(fieldNumber) {
    override fun encode(value: Int): MutableList<DataToken> {
        return mutableListOf(Varint(value.toLong()))
    }

    override fun decode(tokens: List<DataToken>): Int {
        return (tokens.firstOrNull() as? Varint)?.value?.toInt() ?: defaultValue
    }

    override val defaultValue: Int = 0

    companion object {
        operator fun get(fieldNumber: Int) = PbInt32(fieldNumber)
    }
}

class PbRepeatedInt32(
    fieldNumber: Int,
    val encodePacked: Boolean = true
) : PbType<List<Int>>(fieldNumber) {
    override fun encode(value: List<Int>): MutableList<DataToken> {
        return if (encodePacked) {
            val buffer = Buffer()
            value.forEach { it.encodeVarintToSink(buffer) }
            mutableListOf(LengthDelimited(buffer.readByteArray()))
        } else {
            value.map { int -> Varint(int.toLong()) }.toMutableList()
        }
    }

    override fun decode(tokens: List<DataToken>): List<Int> {
        val result = mutableListOf<Int>()
        tokens.forEach {
            when (it) {
                is Varint -> result.add(it.value.toInt())
                is LengthDelimited -> {
                    val buffer = Buffer().apply {
                        write(it.dataBlock)
                    }
                    while (!buffer.exhausted()) {
                        val intValue = buffer.readVarint32()
                        result.add(intValue)
                    }
                }
            }
        }
        return result
    }

    override val defaultValue: List<Int> = emptyList()

    companion object {
        operator fun get(fieldNumber: Int) = PbRepeatedInt32(fieldNumber)
    }
}

class PbInt64(fieldNumber: Int) : PbType<Long>(fieldNumber) {
    override fun encode(value: Long): MutableList<DataToken> {
        return mutableListOf(Varint(value))
    }

    override fun decode(tokens: List<DataToken>): Long {
        return (tokens.firstOrNull() as? Varint)?.value ?: defaultValue
    }

    override val defaultValue: Long = 0L

    companion object {
        operator fun get(fieldNumber: Int) = PbInt64(fieldNumber)
    }
}

class PbRepeatedInt64(
    fieldNumber: Int,
    val encodePacked: Boolean = true
) : PbType<List<Long>>(fieldNumber) {
    override fun encode(value: List<Long>): MutableList<DataToken> {
        return if (encodePacked) {
            val buffer = Buffer()
            value.forEach { it.encodeVarintToSink(buffer) }
            mutableListOf(LengthDelimited(buffer.readByteArray()))
        } else {
            value.map { long -> Varint(long) }.toMutableList()
        }
    }

    override fun decode(tokens: List<DataToken>): List<Long> {
        val result = mutableListOf<Long>()
        tokens.forEach {
            when (it) {
                is Varint -> result.add(it.value)
                is LengthDelimited -> {
                    val buffer = Buffer().apply {
                        write(it.dataBlock)
                    }
                    while (!buffer.exhausted()) {
                        val longValue = buffer.readVarint64()
                        result.add(longValue)
                    }
                }
            }
        }
        return result
    }

    override val defaultValue: List<Long> = emptyList()

    companion object {
        operator fun get(fieldNumber: Int) = PbRepeatedInt64(fieldNumber)
    }
}

class PbBoolean(fieldNumber: Int) : PbType<Boolean>(fieldNumber) {
    override fun encode(value: Boolean): MutableList<DataToken> {
        return mutableListOf(Varint(if (value) 1L else 0L))
    }

    override fun decode(tokens: List<DataToken>): Boolean {
        val intValue = (tokens.firstOrNull() as? Varint)?.value?.toInt() ?: return defaultValue
        return intValue != 0
    }

    override val defaultValue: Boolean = false

    companion object {
        operator fun get(fieldNumber: Int) = PbBoolean(fieldNumber)
    }
}

class PbBytes(fieldNumber: Int) : PbType<ByteArray>(fieldNumber) {
    override fun encode(value: ByteArray): MutableList<DataToken> {
        return mutableListOf(LengthDelimited(value))
    }

    override fun decode(tokens: List<DataToken>): ByteArray {
        return (tokens.firstOrNull() as? LengthDelimited)?.dataBlock ?: defaultValue
    }

    override val defaultValue: ByteArray = ByteArray(0)

    companion object {
        operator fun get(fieldNumber: Int) = PbBytes(fieldNumber)
    }
}

class PbRepeatedBytes(fieldNumber: Int) : PbType<List<ByteArray>>(fieldNumber) {
    override fun encode(value: List<ByteArray>): MutableList<DataToken> {
        return value.map { bytes -> LengthDelimited(bytes) }.toMutableList()
    }

    override fun decode(tokens: List<DataToken>): List<ByteArray> {
        return tokens.mapNotNull { (it as? LengthDelimited)?.dataBlock }
    }

    override val defaultValue: List<ByteArray> = emptyList()

    companion object {
        operator fun get(fieldNumber: Int) = PbRepeatedBytes(fieldNumber)
    }
}

class PbString(fieldNumber: Int) : PbType<String>(fieldNumber) {
    override fun encode(value: String): MutableList<DataToken> {
        return mutableListOf(LengthDelimited(value.encodeToByteArray()))
    }

    override fun decode(tokens: List<DataToken>): String {
        val byteArray = (tokens.firstOrNull() as? LengthDelimited)?.dataBlock ?: return defaultValue
        return byteArray.decodeToString()
    }

    override val defaultValue: String = ""

    companion object {
        operator fun get(fieldNumber: Int) = PbString(fieldNumber)
    }
}

class PbRepeatedString(fieldNumber: Int) : PbType<List<String>>(fieldNumber) {
    override fun encode(value: List<String>): MutableList<DataToken> {
        return value.map { str -> LengthDelimited(str.encodeToByteArray()) }.toMutableList()
    }

    override fun decode(tokens: List<DataToken>): List<String> {
        return tokens.mapNotNull { (it as? LengthDelimited)?.dataBlock?.decodeToString() }
    }

    override val defaultValue: List<String> = emptyList()

    companion object {
        operator fun get(fieldNumber: Int) = PbRepeatedString(fieldNumber)
    }
}

class PbMessage<S : PbSchema>(fieldNumber: Int, val schema: S) : PbType<PbObject<S>>(fieldNumber) {
    override fun encode(value: PbObject<S>): MutableList<DataToken> {
        val byteArray = value.tokens.encodeToBuffer().readByteArray()
        return mutableListOf(LengthDelimited(byteArray))
    }

    override fun decode(tokens: List<DataToken>): PbObject<S> {
        val byteArray = (tokens.firstOrNull() as? LengthDelimited)?.dataBlock ?: return defaultValue
        return PbObject(schema, byteArray)
    }

    override val defaultValue: PbObject<S> = PbObject(schema) { }
}

operator fun <S : PbSchema> S.get(fieldNumber: Int) = PbMessage(fieldNumber, this)

class PbRepeatedMessage<S : PbSchema>(fieldNumber: Int, val schema: S) : PbType<List<PbObject<S>>>(fieldNumber) {
    override fun encode(value: List<PbObject<S>>): MutableList<DataToken> {
        return value.map { obj ->
            val byteArray = obj.tokens.encodeToBuffer().readByteArray()
            LengthDelimited(byteArray)
        }.toMutableList()
    }

    override fun decode(tokens: List<DataToken>): List<PbObject<S>> {
        return tokens.mapNotNull {
            val byteArray = (it as? LengthDelimited)?.dataBlock ?: return@mapNotNull null
            PbObject(schema, byteArray)
        }
    }

    override val defaultValue: List<PbObject<S>> = emptyList()
}

object PbRepeated {
    operator fun <S : PbSchema> get(pbMsg: PbMessage<S>) = PbRepeatedMessage(pbMsg.fieldNumber, pbMsg.schema)
}

class PbOptional<T>(private val wrapped: PbType<T>) : PbType<T?>(wrapped.fieldNumber) {
    override fun encode(value: T?): MutableList<DataToken> {
        return if (value == null) {
            mutableListOf()
        } else {
            wrapped.encode(value)
        }
    }

    override fun decode(tokens: List<DataToken>): T? {
        if (tokens.isEmpty()) return null
        return wrapped.decode(tokens)
    }

    override val defaultValue: T? = null

    companion object {
        operator fun <T> get(pbType: PbType<T>) = PbOptional(pbType)
    }
}