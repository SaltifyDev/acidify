package org.ntqqrev.yogurt.util

import io.ktor.server.application.*

/**
 * 配置缓存依赖已移除
 * 现在直接使用 Bot 的缓存 API
 */
fun Application.configureCacheDeps() {
    // 不再需要配置缓存依赖
}