package org.ntqqrev.acidify.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.ntqqrev.acidify.internal.util.toHex
import kotlin.jvm.JvmField
import kotlin.random.Random

@Serializable
internal class SessionStore(
    @JvmField var uin: Long,
    @JvmField var uid: String,

    var a2: ByteArray,
    var d2: ByteArray,
    var d2Key: ByteArray,
    var tgtgt: ByteArray,
    var encryptedA1: ByteArray,
    var noPicSig: ByteArray,

    var qrSig: ByteArray,

    val guid: ByteArray,
    val deviceName: String,
) {
    @Transient internal var keySig: ByteArray? = null
    @Transient internal var exchangeKey: ByteArray? = null
    @Transient internal var unusualCookies: String? = null

    companion object {
        fun empty(): SessionStore {
            return SessionStore(
                uin = 0,
                uid = "",
                a2 = ByteArray(0),
                d2 = ByteArray(0),
                d2Key = ByteArray(16),
                tgtgt = ByteArray(0),
                encryptedA1 = ByteArray(0),
                noPicSig = ByteArray(0),
                qrSig = ByteArray(0),
                guid = Random.nextBytes(16),
                deviceName = "Lagrange-${Random.nextBytes(3).toHex()}"
            )
        }
    }

    fun clear() {
        a2 = ByteArray(0)
        d2 = ByteArray(0)
        d2Key = ByteArray(16)
        tgtgt = ByteArray(0)
        encryptedA1 = ByteArray(0)
        noPicSig = ByteArray(0)
        qrSig = ByteArray(0)
        keySig = null
        exchangeKey = null
        unusualCookies = null
    }
}