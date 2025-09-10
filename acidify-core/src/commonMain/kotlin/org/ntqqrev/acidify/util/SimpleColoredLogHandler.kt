package org.ntqqrev.acidify.util

import com.github.ajalt.mordant.rendering.TextColors.*
import org.ntqqrev.acidify.common.log.LogHandler
import org.ntqqrev.acidify.common.log.LogLevel

object SimpleColoredLogHandler : LogHandler {
    private fun shortenPackageName(tag: String): String {
        val parts = tag.split('.')
        val b = StringBuilder()
        for (i in 0 until parts.size - 1) {
            b.append(parts[i][0])
            b.append('.')
        }
        b.append(parts.last())
        b.padEnd(30)
        return b.toString()
    }

    override fun handleLog(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        val b = StringBuilder()
        b.append(
            when (level) {
                LogLevel.VERBOSE -> gray("TRACE")
                LogLevel.DEBUG -> brightBlue("DEBUG")
                LogLevel.INFO -> brightGreen(" INFO")
                LogLevel.WARN -> brightYellow(" WARN")
                LogLevel.ERROR -> brightRed("ERROR")
            }
        )
        b.append(" ")
        b.append(
            when (level) {
                LogLevel.VERBOSE -> gray
                LogLevel.ERROR -> brightRed
                else -> brightMagenta
            }(shortenPackageName(tag))
        )
        b.append(" ")
        b.append(
            when (level) {
                LogLevel.VERBOSE -> gray(message)
                LogLevel.ERROR -> brightRed(message)
                else -> message
            }
        )
        if (throwable != null) {
            b.append("\n")
            b.append(
                when (level) {
                    LogLevel.ERROR -> brightRed
                    LogLevel.WARN -> brightYellow
                    else -> gray
                }(throwable.stackTraceToString())
            )
        }
        println(b.toString())
    }
}