package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupListOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val GetGroupList = ApiEndpoint.GetGroupList {
    val groupCache = application.dependencies.resolve<GroupCache>()
    val groups = groupCache.getAll(cacheFirst = !it.noCache)
    GetGroupListOutput(
        groups = groups.map(BotGroupData::toMilkyEntity)
    )
}