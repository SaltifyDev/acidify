package org.ntqqrev.acidify.internal.service

import org.ntqqrev.acidify.exception.OidbException
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.Oidb
import org.ntqqrev.acidify.pb.invoke

internal abstract class OidbService<T, R>(
    val oidbCommand: Int,
    val oidbService: Int
) : Service<T, R>("OidbSvcTrpcTcp.0x${oidbCommand.toString(16)}_$oidbService") {
    abstract fun buildOidb(client: LagrangeClient, payload: T): ByteArray
    abstract fun parseOidb(client: LagrangeClient, payload: ByteArray): R

    override fun build(client: LagrangeClient, payload: T): ByteArray = Oidb {
        it[command] = oidbCommand
        it[service] = oidbService
        it[body] = buildOidb(client, payload)
    }.toByteArray()

    override fun parse(client: LagrangeClient, payload: ByteArray): R {
        val response = Oidb(payload)
        val oidbResult = response.get { result }
        if (oidbResult != 0) {
            throw OidbException(oidbCommand, oidbService, oidbResult, response.get { message })
        }
        return parseOidb(client, response.get { body })
    }
}

internal abstract class NoInputOidbService<R>(
    oidbCmd: Int,
    oidbSubCmd: Int
) : OidbService<Unit, R>(oidbCmd, oidbSubCmd)

internal abstract class NoOutputOidbService<T>(
    oidbCmd: Int,
    oidbSubCmd: Int
) : OidbService<T, Unit>(oidbCmd, oidbSubCmd) {
    override fun parseOidb(client: LagrangeClient, payload: ByteArray) = Unit
}