package org.ntqqrev.acidify.util

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.*

internal class PrintlnLogWriter : LogWriter() {
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

    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        val b = StringBuilder()
        b.append(
            when (severity) {
                Severity.Verbose -> gray("TRACE")
                Severity.Debug -> brightBlue("DEBUG")
                Severity.Info -> brightGreen(" INFO")
                Severity.Warn -> brightYellow(" WARN")
                Severity.Error -> brightRed("ERROR")
                Severity.Assert -> brightRed("FATAL")
            }
        )
        b.append(" ")
        b.append(
            when (severity) {
                Severity.Verbose -> gray
                Severity.Error, Severity.Assert -> brightRed
                else -> magenta
            }(shortenPackageName(tag))
        )
        b.append(" ")
        b.append(
            when (severity) {
                Severity.Verbose -> gray(message)
                Severity.Error, Severity.Assert -> brightRed(message)
                else -> message
            }
        )
        if (throwable != null) {
            b.append("\n")
            b.append(when (severity) {
                Severity.Error, Severity.Assert -> brightRed
                Severity.Warn -> brightYellow
                else -> gray
            }(throwable.stackTraceToString()))
        }
        println(b.toString())
    }
}

internal fun createLogger(obj: Any): Logger = Logger(
    loggerConfigInit(PrintlnLogWriter()),
    obj::class.qualifiedName ?: throw IllegalArgumentException("Cannot create logger for anonymous class")
)