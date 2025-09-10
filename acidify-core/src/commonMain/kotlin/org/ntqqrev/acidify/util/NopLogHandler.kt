package org.ntqqrev.acidify.util

import org.ntqqrev.acidify.common.log.LogHandler
import org.ntqqrev.acidify.common.log.LogLevel

object NopLogHandler : LogHandler {
    override fun handleLog(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        // do nothing
    }
}