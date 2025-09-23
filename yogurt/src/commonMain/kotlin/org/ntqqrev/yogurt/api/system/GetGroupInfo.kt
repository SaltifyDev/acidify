package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.YogurtCache

val GetGroupInfo = ApiEndpoint.GetGroupInfo {
    val groupCache = application.dependencies.resolve<YogurtCache<Long, BotGroupData>>("GroupCache")
    val group = groupCache[it.groupId, !it.noCache]
        ?: throw MilkyApiException(-404, "Group not found")
    GetGroupInfoOutput(
        group = group.toMilkyEntity()
    )
}