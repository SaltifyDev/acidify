package org.ntqqrev.yogurt.util

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.QRCodeGeneratedEvent
import org.ntqqrev.yogurt.YogurtApp.qrCodePath
import org.ntqqrev.yogurt.qrcode.ErrorCorrectionLevel
import org.ntqqrev.yogurt.qrcode.QRCodeProcessor

object Palette {
    const val WHITE_WHITE = '\u2588'
    const val WHITE_BLACK = '\u2580'
    const val BLACK_WHITE = '\u2584'
    const val BLACK_BLACK = ' '
}

fun generateTerminalQRCode(data: String): String {
    val matrix = QRCodeProcessor(data, ErrorCorrectionLevel.LOW).encode()
    val height = matrix.size
    val width = matrix[0].size

    val b = StringBuilder()

    for (row in 0 until height step 2) {
        for (col in 0 until width) {
            val upper = matrix[col][row].dark
            val lower = if (row + 1 < height) matrix[col][row + 1].dark else false
            val char = when {
                upper && lower -> Palette.WHITE_WHITE
                upper && !lower -> Palette.WHITE_BLACK
                !upper && lower -> Palette.BLACK_WHITE
                else -> Palette.BLACK_BLACK
            }
            b.append(char)
        }
        b.appendLine()
    }

    return b.toString()
}

fun Application.configureQRCodeDisplay() {
    launch {
        val bot = dependencies.resolve<Bot>()
        val logger = bot.createLogger("QRCode")
        bot.eventFlow.filterIsInstance<QRCodeGeneratedEvent>().collect {
            logger.i { "请用手机 QQ 扫描二维码：\n" + generateTerminalQRCode(it.url) }
            logger.i { "或使用以下 URL 生成二维码并扫描：" }
            logger.i { it.url }
            SystemFileSystem.sink(qrCodePath).buffered().use { sink ->
                sink.write(it.png)
            }
            logger.i { "二维码文件已保存至 ${SystemFileSystem.resolve(qrCodePath)}" }
        }
    }
}