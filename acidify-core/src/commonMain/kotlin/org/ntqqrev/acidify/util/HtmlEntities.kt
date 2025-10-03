package org.ntqqrev.acidify.util

object HtmlEntities {
    private val entities = mapOf(
        "nbsp" to "\u00A0",
        "lt" to "<",
        "gt" to ">",
        "amp" to "&",
        "quot" to "\"",
        "apos" to "'",
    )

    fun unescape(input: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            if (c == '&') {
                val semicolon = input.indexOf(';', i + 1)
                if (semicolon > 0) {
                    val entity = input.substring(i + 1, semicolon)
                    val decoded = when {
                        entity.startsWith("#x") || entity.startsWith("#X") -> {
                            // 处理十六进制 &#xA0;
                            entity.drop(2).toIntOrNull(16)?.toChar()?.toString()
                        }
                        entity.startsWith("#") -> {
                            // 处理十进制 &#160;
                            entity.drop(1).toIntOrNull()?.toChar()?.toString()
                        }
                        else -> entities[entity]
                    }
                    if (decoded != null) {
                        sb.append(decoded)
                        i = semicolon + 1
                        continue
                    }
                }
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }
}