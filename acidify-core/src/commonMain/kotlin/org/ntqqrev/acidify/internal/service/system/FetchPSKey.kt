package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.FetchPsKeyReq
import org.ntqqrev.acidify.internal.packet.oidb.FetchPsKeyResp
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.invoke

internal object FetchPSKey : OidbService<List<String>, Map<String, String>>(0x102a, 0) {
    override fun buildOidb(client: LagrangeClient, payload: List<String>): ByteArray =
        FetchPsKeyReq {
            it[domains] = payload
        }.toByteArray()

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): Map<String, String> =
        FetchPsKeyResp(payload)
            .get { psKeyEntries }
            .associate { it.get { domain } to it.get { key } }
}