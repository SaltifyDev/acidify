package org.ntqqrev.acidify.util.log

fun interface LogHandler {
    fun handleLog(level: LogLevel, tag: String, message: String, throwable: Throwable?)
}