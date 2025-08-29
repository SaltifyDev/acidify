package org.ntqqrev.acidify.internal.util

private const val HEX = "1234567890abcdef"

internal fun generateTrace(): String {
    val b = StringBuilder(55)

    b.append("00") // 2 chars
    b.append('-') // 1 char

    repeat(32) { b.append(HEX.random()) } // 32 chars
    b.append('-') // 1 char

    repeat(16) { b.append(HEX.random()) } // 16 chars
    b.append('-') // 1 char

    b.append("01") // 2 chars

    return b.toString()
}