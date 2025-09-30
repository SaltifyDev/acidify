package org.ntqqrev.acidify.message

enum class ImageFormat(val ext: String) {
    PNG("png"),
    GIF("gif"),
    JPEG("jpg"),
    BMP("bmp"),
    WEBP("webp"),
    TIFF("tiff");

    companion object {
        fun fromExtension(ext: String): ImageFormat? {
            return entries.find { it.ext.equals(ext, ignoreCase = true) }
        }
    }
}