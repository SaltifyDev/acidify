package org.ntqqrev.acidify.common.log

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot

typealias MessageSupplier = () -> String

internal class Logger(private val bot: Bot, val tag: String) {
    private fun MutableSharedFlow<LogMessage>.emitAsync(message: LogMessage) {
        bot.client.scope.launch {
            emit(message)
        }
    }

    fun v(messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.VERBOSE, tag, messageSupplier)
    )

    fun d(messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.DEBUG, tag, messageSupplier)
    )

    fun i(messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.INFO, tag, messageSupplier)
    )

    fun w(messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.WARN, tag, messageSupplier)
    )

    fun w(t: Throwable, messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.WARN, tag, messageSupplier, t)
    )

    fun e(messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.ERROR, tag, messageSupplier)
    )

    fun e(t: Throwable, messageSupplier: MessageSupplier) = bot.sharedLogFlow.emitAsync(
        LogMessage(LogLevel.ERROR, tag, messageSupplier, t)
    )
}