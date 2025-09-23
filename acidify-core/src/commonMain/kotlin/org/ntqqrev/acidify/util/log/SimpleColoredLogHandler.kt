package org.ntqqrev.acidify.util.log

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.bold
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

    private val timeFormat = LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    @OptIn(ExperimentalTime::class)
    override fun handleLog(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        val b = StringBuilder()
        val now: Instant = Clock.System.now()
        val localNow: LocalDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        b.append(bold(green(timeFormat.format(localNow))))
        b.append(" ")
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
                else -> cyan
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