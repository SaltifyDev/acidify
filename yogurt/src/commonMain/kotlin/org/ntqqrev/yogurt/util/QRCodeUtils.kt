package org.ntqqrev.yogurt.util

import org.ntqqrev.acidify.qrcode.ErrorCorrectionLevel
import org.ntqqrev.acidify.qrcode.QRCodeProcessor

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