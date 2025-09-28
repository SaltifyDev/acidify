package org.ntqqrev.yogurt.api.system

import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupMemberListOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke
import org.ntqqrev.yogurt.util.resolveGroupMemberCache

val GetGroupMemberList = ApiEndpoint.GetGroupMemberList {
    val memberCache = application.resolveGroupMemberCache(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")
    val members = memberCache.getAll(cacheFirst = !it.noCache)
    GetGroupMemberListOutput(
        members = members.map { member -> member.toMilkyEntity(it.groupId) }
    )
}