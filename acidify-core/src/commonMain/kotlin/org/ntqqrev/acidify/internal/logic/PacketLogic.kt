package org.ntqqrev.acidify.internal.logic

import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.collections.ConcurrentMap
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.writePacket
import korlibs.io.compression.deflate.ZLib
import korlibs.io.compression.uncompress
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.internal.LagrangeClient
import kotlin.random.Random
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.lagrange.library.crypto.tea.TeaProvider
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.internal.packet.SsoResponse
import org.ntqqrev.acidify.internal.packet.system.SsoReservedFields
import org.ntqqrev.acidify.internal.packet.system.SsoSecureInfo
import org.ntqqrev.acidify.internal.util.*
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke
import org.ntqqrev.acidify.util.createLogger

internal class PacketLogic(client: LagrangeClient) : AbstractLogic(client) {
    private var sequence = Random.nextInt(0x10000, 0x20000)
    private val host = "msfwifi.3g.qq.com"
    private val port = 8080

    private val selectorManager = SelectorManager(client.scope.coroutineContext)
    private val socket = aSocket(selectorManager).tcp()
    private lateinit var input: ByteReadChannel
    private lateinit var output: ByteWriteChannel
    private val pending = ConcurrentMap<Int, CompletableDeferred<SsoResponse>>()
    private val headerLength = 4
    var connected = false
    val signRequiredCommand = setOf(
        "trpc.o3.ecdh_access.EcdhAccess.SsoEstablishShareKey",
        "trpc.o3.ecdh_access.EcdhAccess.SsoSecureAccess",
        "trpc.o3.report.Report.SsoReport",
        "MessageSvc.PbSendMsg",
        "wtlogin.trans_emp",
        "wtlogin.login",
        "trpc.login.ecdh.EcdhService.SsoKeyExchange",
        "trpc.login.ecdh.EcdhService.SsoNTLoginPasswordLogin",
        "trpc.login.ecdh.EcdhService.SsoNTLoginEasyLogin",
        "trpc.login.ecdh.EcdhService.SsoNTLoginPasswordLoginNewDevice",
        "trpc.login.ecdh.EcdhService.SsoNTLoginEasyLoginUnusualDevice",
        "trpc.login.ecdh.EcdhService.SsoNTLoginPasswordLoginUnusualDevice",
        "OidbSvcTrpcTcp.0x11ec_1",
        "OidbSvcTrpcTcp.0x758_1", // create a group
        "OidbSvcTrpcTcp.0x7c1_1",
        "OidbSvcTrpcTcp.0x7c2_5", // request friend
        "OidbSvcTrpcTcp.0x10db_1",
        "OidbSvcTrpcTcp.0x8a1_7", // request group
        "OidbSvcTrpcTcp.0x89a_0",
        "OidbSvcTrpcTcp.0x89a_15",
        "OidbSvcTrpcTcp.0x88d_0", // fetch group detail
        "OidbSvcTrpcTcp.0x88d_14",
        "OidbSvcTrpcTcp.0x112a_1",
        "OidbSvcTrpcTcp.0x587_74",
        "OidbSvcTrpcTcp.0x1100_1",
        "OidbSvcTrpcTcp.0x1102_1",
        "OidbSvcTrpcTcp.0x1103_1",
        "OidbSvcTrpcTcp.0x1107_1",
        "OidbSvcTrpcTcp.0x1105_1",
        "OidbSvcTrpcTcp.0xf88_1",
        "OidbSvcTrpcTcp.0xf89_1",
        "OidbSvcTrpcTcp.0xf57_1",
        "OidbSvcTrpcTcp.0xf57_106",
        "OidbSvcTrpcTcp.0xf57_9",
        "OidbSvcTrpcTcp.0xf55_1",
        "OidbSvcTrpcTcp.0xf67_1",
        "OidbSvcTrpcTcp.0xf67_5",
        "OidbSvcTrpcTcp.0x6d9_4"
    )

    private val logger = createLogger(this)

    suspend fun connect() {
        val s = socket.connect(host, port)
        input = s.openReadChannel()
        output = s.openWriteChannel(autoFlush = true)
        logger.i { "Connected to $host:$port" }
        connected = true

        client.scope.launch {
            handleReceiveLoop()
        }

        client.scope.launch {
            while (connected) {
                // TODO: client.callService(SendHeartbeat)
                delay(300_000) // 5 minutes
            }
        }
    }

    suspend fun disconnect() {
        input.cancel()
        output.flushAndClose()
        connected = false
    }

    suspend fun sendPacket(cmd: String, payload: ByteArray): SsoResponse {
        val sequence = this.sequence++
        val sso = buildSso(cmd, payload, sequence)
        val service = buildService(sso)

        val deferred = CompletableDeferred<SsoResponse>()
        pending[sequence] = deferred

        output.writePacket(service)
        logger.d { "[seq=$sequence] -> $cmd" }

        return deferred.await()
    }

    private suspend fun handleReceiveLoop() {
        while (connected) {
            try {
                val header = input.readByteArray(headerLength)
                val packetLength = header.readUInt32BE(0)
                val packet = input.readByteArray(packetLength.toInt() - 4)
                val service = parseService(packet)
                val sso = parseSso(service)
                logger.d { "[seq=${sso.sequence}] <- ${sso.command} (code=${sso.retCode})" }
                pending.remove(sso.sequence).also {
                    if (it != null) {
                        it.complete(sso)
                    } else {
                        // TODO: client.eventContext.process(sso)
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Error receiving packet" }
                disconnect()
            }
        }
    }

    private fun buildService(sso: ByteArray): Buffer {
        val packet = Buffer()

        packet.barrier(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX) {
            writeInt(12)
            writeByte(if (client.sessionStore.d2.isEmpty()) 2 else 1)
            writeBytes(client.sessionStore.d2, Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
            writeByte(0) // unknown
            writeString(client.sessionStore.uin.toString(), Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
            writeBytes(TeaProvider.encrypt(sso, client.sessionStore.d2Key))
        }

        return packet
    }

    val buildSsoFixedBytes = byteArrayOf(
        0x02, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00,
    )

    private suspend fun buildSso(command: String, payload: ByteArray, sequence: Int): ByteArray {
        val packet = Buffer()
        val ssoReserved = buildSsoReserved(command, payload, sequence)

        packet.barrier(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX) {
            writeInt(sequence)
            writeInt(client.appInfo.subAppId)
            writeInt(2052)  // locale id
            writeFully(buildSsoFixedBytes)
            writeBytes(client.sessionStore.a2, Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
            writeString(command, Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
            writeBytes(ByteArray(0), Prefix.UINT_32 or Prefix.INCLUDE_PREFIX) // unknown
            writeString(client.sessionStore.guid.toHex(), Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
            writeBytes(ByteArray(0), Prefix.UINT_32 or Prefix.INCLUDE_PREFIX) // unknown
            writeString(client.appInfo.currentVersion, Prefix.UINT_16 or Prefix.INCLUDE_PREFIX)
            writeBytes(ssoReserved, Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
        }

        packet.writeBytes(payload, Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)

        return packet.readByteArray()
    }

    private suspend fun buildSsoReserved(command: String, payload: ByteArray, sequence: Int): ByteArray {
        val result: SignProvider.Result? = if (signRequiredCommand.contains(command)) {
            client.signProvider.sign(command, sequence, payload)
        } else null

        return SsoReservedFields {
            it[trace] = generateTrace()
            it[uid] = client.sessionStore.uid
            it[secureInfo] = result?.toSsoSecureInfo()
        }.toByteArray()
    }

    private fun parseSso(packet: ByteArray): SsoResponse {
        val reader = packet.reader()
        reader.readUInt() // headLen
        val sequence = reader.readUInt()
        val retCode = reader.readInt()
        val extra = reader.readPrefixedString(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
        val command = reader.readPrefixedString(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)
        reader.readPrefixedBytes(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX) // messageCookie
        val isCompressed = reader.readInt() == 1
        reader.readPrefixedBytes(Prefix.UINT_32) // reservedField
        var payload = reader.readPrefixedBytes(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)

        if (isCompressed) {
            payload = ZLib.uncompress(payload)
        }

        return if (retCode == 0) {
            SsoResponse(retCode, command, payload, sequence.toInt())
        } else {
            SsoResponse(retCode, command, payload, sequence.toInt(), extra)
        }
    }

    private fun parseService(raw: ByteArray): ByteArray {
        val reader = raw.reader()

        val protocol = reader.readUInt()
        val authFlag = reader.readByte()
        /* val flag = */ reader.readByte()
        /* val uin = */ reader.readPrefixedString(Prefix.UINT_32 or Prefix.INCLUDE_PREFIX)

        if (protocol != 12u && protocol != 13u) throw Exception("Unrecognized protocol: $protocol")

        val encrypted = reader.readByteArray()
        return when (authFlag) {
            0.toByte() -> encrypted
            1.toByte() -> TeaProvider.decrypt(encrypted, client.sessionStore.d2Key)
            2.toByte() -> TeaProvider.decrypt(encrypted, ByteArray(16))
            else -> throw Exception("Unrecognized auth flag: $authFlag")
        }
    }

    private fun SignProvider.Result.toSsoSecureInfo(): PbObject<SsoSecureInfo> {
        return SsoSecureInfo {
            it[sign] = this@toSsoSecureInfo.sign
            it[token] = this@toSsoSecureInfo.token
            it[extra] = this@toSsoSecureInfo.extra
        }
    }
}