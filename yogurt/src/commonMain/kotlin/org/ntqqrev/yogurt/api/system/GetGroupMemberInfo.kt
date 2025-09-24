package org.ntqqrev.yogurt.api.system

import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupMemberInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.resolveGroupMemberCache

val GetGroupMemberInfo = ApiEndpoint.GetGroupMemberInfo {
    val memberCache = application.resolveGroupMemberCache(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")
    val member = memberCache[it.userId, !it.noCache]
        ?: throw MilkyApiException(-404, "Group member not found")
    GetGroupMemberInfoOutput(
        member = member.toMilkyEntity(it.groupId)
    )
}