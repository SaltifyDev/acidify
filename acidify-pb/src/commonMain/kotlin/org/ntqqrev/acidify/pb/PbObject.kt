package org.ntqqrev.acidify.pb

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.ntqqrev.acidify.pb.dataview.DataToken
import org.ntqqrev.acidify.pb.util.MultiMap
import org.ntqqrev.acidify.pb.util.encodeToBuffer
import org.ntqqrev.acidify.pb.util.multiMapOf
import org.ntqqrev.acidify.pb.util.readTokens
import kotlin.jvm.JvmField

class PbObject<S : PbSchema>(
    @JvmField val schema: S,
    @JvmField internal val tokens: MultiMap<Int, DataToken> = multiMapOf()
) {
    constructor(schema: S, byteArray: ByteArray) : this(
        schema,
        Buffer().apply {
            write(byteArray)
        }.readTokens()
    )

    operator fun <T> get(type: PbType<T>): T {
        val tokenList = tokens[type.fieldNumber] ?: return type.defaultValue
        return type.decode(tokenList)
    }

    inline fun <T> get(supplier: S.() -> PbType<T>): T {
        val type = schema.supplier()
        return get(type)
    }

    operator fun <T> set(type: PbType<T>, value: T) {
        tokens[type.fieldNumber] = type.encode(value)
    }

    inline fun <T> set(supplier: S.() -> Pair<PbType<T>, T>) {
        val (type, value) = schema.supplier()
        set(type, value)
    }

    fun toByteArray(): ByteArray {
        return tokens.encodeToBuffer().readByteArray()
    }
}

inline fun <S : PbSchema> PbObject(schema: S, block: S.(PbObject<S>) -> Unit): PbObject<S> {
    val obj = PbObject(schema)
    schema.block(obj)
    return obj
}