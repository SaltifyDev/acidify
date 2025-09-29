package org.ntqqrev.acidify.util.log

fun shortenPackageName(tag: String): String {
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