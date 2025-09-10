package org.ntqqrev.acidify.common.log

fun interface LogHandler {
    fun handleLog(level: LogLevel, tag: String, message: String, throwable: Throwable?)
}