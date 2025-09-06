package org.ntqqrev.acidify.internal.service.system

import kotlinx.io.*
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.login.TlvQrCode
import org.ntqqrev.acidify.internal.service.NoInputService
import org.ntqqrev.acidify.internal.util.Prefix
import org.ntqqrev.acidify.internal.util.reader
import org.ntqqrev.acidify.internal.util.writeBytes
import org.ntqqrev.acidify.pb.invoke

internal object FetchQrCode : NoInputService<FetchQrCode.Result>("wtlogin.trans_emp") {
    override fun build(client: LagrangeClient, payload: Unit): ByteArray {
        val tlvPack = TlvQrCode(client).apply {
            tlv16()
            tlv1b()
            tlv1d()
            tlv33()
            tlv35()
            tlv66()
            tlvD1()
        }
        val packet = Buffer().apply {
            writeUShort(0u)
            writeUInt(client.appInfo.appId.toUInt())
            writeULong(0u) // uin
            writeBytes(ByteArray(0))
            writeByte(0)
            writeBytes(ByteArray(0), Prefix.UINT_16 or Prefix.LENGTH_ONLY)
            writeBytes(tlvPack.build())
        }
        return client.loginLogic.buildCode2DPacket(packet.readByteArray(), 0x31u)
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): Result {
        val wtLogin = client.loginLogic.parseWtLogin(payload)
        val code2d = client.loginLogic.parseCode2DPacket(wtLogin)
        val reader = code2d.reader()
        reader.discard(1)
        val sig = reader.readPrefixedBytes(Prefix.UINT_16 or Prefix.LENGTH_ONLY)
        val tlv = client.loginLogic.readTlv(reader)
        client.sessionStore.qrSig = sig
        val respD1Body = TlvQrCode.Companion.BodyD1Response(tlv.getValue(0xD1u))
        return Result(
            qrCodeUrl = respD1Body.get { qrCodeUrl },
            qrCodePng = tlv.getValue(0x17u)
        )
    }

    class Result(
        val qrCodeUrl: String,
        val qrCodePng: ByteArray
    )
}