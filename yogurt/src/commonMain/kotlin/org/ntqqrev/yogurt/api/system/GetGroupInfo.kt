package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val GetGroupInfo = ApiEndpoint.GetGroupInfo {
    val groupCache = application.dependencies.resolve<GroupCache>()
    val group = groupCache[it.groupId, !it.noCache]
        ?: throw MilkyApiException(-404, "Group not found")
    GetGroupInfoOutput(
        group = group.toMilkyEntity()
    )
}