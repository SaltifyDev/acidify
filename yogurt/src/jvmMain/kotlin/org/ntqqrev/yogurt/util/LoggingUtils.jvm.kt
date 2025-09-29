package org.ntqqrev.yogurt.util

import io.ktor.util.logging.*
import org.ntqqrev.acidify.util.log.LogHandler
import org.ntqqrev.acidify.util.log.LogLevel
import org.ntqqrev.acidify.util.log.shortenPackageName

private fun Logger.toAcidifyLogHandler(): LogHandler =
    LogHandler { level, tag, message, throwable ->
        val trueMessage = "[${shortenPackageName(tag)}] $message"
        when (level) {
            LogLevel.VERBOSE -> trace(trueMessage)
            LogLevel.DEBUG -> debug(trueMessage)
            LogLevel.INFO -> info(trueMessage)
            LogLevel.WARN -> if (throwable == null) {
                warn(trueMessage)
            } else {
                warn(trueMessage, throwable)
            }

            LogLevel.ERROR -> if (throwable == null) {
                error(trueMessage)
            } else {
                error(trueMessage, throwable)
            }
        }
    }

actual val logHandler: LogHandler = KtorSimpleLogger("Core").toAcidifyLogHandler()