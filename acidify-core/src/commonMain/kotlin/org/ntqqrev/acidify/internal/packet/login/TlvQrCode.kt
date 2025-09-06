package org.ntqqrev.acidify.internal.packet.login

import io.ktor.utils.io.core.*
import kotlinx.io.*
import kotlinx.io.Buffer
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.util.Prefix
import org.ntqqrev.acidify.internal.util.barrier
import org.ntqqrev.acidify.internal.util.fromHex
import org.ntqqrev.acidify.internal.util.writeString
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.invoke
import org.ntqqrev.acidify.pb.pb

internal class TlvQrCode(val client: LagrangeClient) {
    private val builder = Buffer()

    private var tlvCount: UShort = 0u

    fun tlv16() = defineTlv(0x16u) {
        writeUInt(0u)
        writeInt(client.appInfo.appId)
        writeInt(client.appInfo.subAppId)
        writeFully(client.sessionStore.guid)
        writeString(client.appInfo.packageName, Prefix.UINT_16 or Prefix.LENGTH_ONLY)
        writeString(client.appInfo.ptVersion, Prefix.UINT_16 or Prefix.LENGTH_ONLY)
        writeString(client.appInfo.packageName, Prefix.UINT_16 or Prefix.LENGTH_ONLY)
    }

    fun tlv1b() = defineTlv(0x1bu) {
        writeUInt(0u) // micro
        writeUInt(0u) // version
        writeUInt(3u) // size
        writeUInt(4u) // margin
        writeUInt(72u) // dpi
        writeUInt(2u) // eclevel
        writeUInt(2u) // hint
        writeUShort(0u) // unknown
    }

    fun tlv1d() = defineTlv(0x1du) {
        writeUByte(1u)
        writeInt(client.appInfo.mainSigMap) // misc bitmap
        writeUInt(0u)
        writeUByte(0u)
    }

    fun tlv33() = defineTlv(0x33u) {
        writeFully(client.sessionStore.guid)
    }

    fun tlv35() = defineTlv(0x35u) {
        writeInt(client.appInfo.ssoVersion)
    }

    fun tlv66() = defineTlv(0x66u) {
        writeInt(client.appInfo.ssoVersion)
    }

    fun tlvD1() = defineTlv(0xd1u) {
        val body = BodyD1 {
            it[system] = BodyD1.System {
                it[os] = client.appInfo.os
                it[deviceName] = client.sessionStore.deviceName
            }
            it[typeBuf] = "3001".fromHex()
        }
        writeFully(body.toByteArray())
    }

    fun build(): ByteArray = Buffer().apply {
        writeUShort(tlvCount)
        writeFully(builder.readByteArray())
    }.readByteArray()

    private fun defineTlv(tag: UShort, tlv: Sink.() -> Unit) {
        tlvCount++

        builder.writeUShort(tag)
        builder.barrier(Prefix.UINT_16 or Prefix.LENGTH_ONLY) {
            tlv()
        }
    }

    companion object {
        object BodyD1 : PbSchema() {
            val system = pb message System field 1
            val typeBuf = pb bytes 4

            object System : PbSchema() {
                val os = pb string 1
                val deviceName = pb string 2
            }
        }

        object BodyD1Response : PbSchema() {
            val qrCodeUrl = pb string 2
            val qrSig = pb string 3
        }
    }
}