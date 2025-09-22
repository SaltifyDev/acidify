package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.common.struct.BotGroupData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupListOutput
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.YogurtCache

val GetGroupList = ApiEndpoint.GetGroupList {
    val groupCache = application.dependencies.resolve<YogurtCache<Long, BotGroupData>>("GroupCache")
    val groups = groupCache.getAll(cacheFirst = !it.noCache)
    GetGroupListOutput(
        groups = groups.map(BotGroupData::toMilkyEntity)
    )
}