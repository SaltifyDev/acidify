package org.ntqqrev.acidify.internal.service.system

import kotlinx.io.*
import org.ntqqrev.acidify.common.QrCodeState
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.service.NoInputService
import org.ntqqrev.acidify.internal.util.Prefix
import org.ntqqrev.acidify.internal.util.reader
import org.ntqqrev.acidify.internal.util.writeBytes

internal object QueryQrCodeState : NoInputService<QrCodeState>("wtlogin.trans_emp") {
    override fun build(client: LagrangeClient, payload: Unit): ByteArray {
        val packet = Buffer().apply {
            writeUShort(0u)
            writeUInt(client.appInfo.appId.toUInt())
            writeBytes(client.sessionStore.qrSig, Prefix.UINT_16 or Prefix.LENGTH_ONLY)
            writeULong(0u) // uin
            writeByte(0)
            writeBytes(ByteArray(0), Prefix.UINT_16 or Prefix.LENGTH_ONLY)
            writeUShort(0u)  // actually it is the tlv count, but there is no tlv so 0x0 is used
        }
        return client.loginLogic.buildCode2DPacket(packet.readByteArray(), 0x12u)
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): QrCodeState {
        val wtlogin = client.loginLogic.parseWtLogin(payload)
        val reader = client.loginLogic.parseCode2DPacket(wtlogin).reader()
        val state = QrCodeState.fromByte(reader.readByte())
        if (state == QrCodeState.CONFIRMED) {
            reader.discard(4)
            client.sessionStore.uin = reader.readUInt().toLong()
            reader.discard(4)

            val tlv = client.loginLogic.readTlv(reader)
            client.sessionStore.tgtgt = tlv[0x1eu]!!
            client.sessionStore.encryptedA1 = tlv[0x18u]!!
            client.sessionStore.noPicSig = tlv[0x19u]!!
        }
        return state
    }
}