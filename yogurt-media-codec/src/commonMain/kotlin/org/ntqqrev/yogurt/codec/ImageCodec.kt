package org.ntqqrev.yogurt.codec

import org.ntqqrev.acidify.message.ImageFormat

data class ImageInfo(
    val format: ImageFormat,
    val width: Int,
    val height: Int,
)

fun getImageInfo(data: ByteArray): ImageInfo = ImageDecoder.decodeImageInfo(data)
    ?: throw IllegalArgumentException("Unsupported or invalid image data")

object ImageDecoder {
    fun decodeImageInfo(data: ByteArray): ImageInfo? {
        if (data.size < 12) return null

        // Check PNG
        if (isPng(data)) {
            return parsePng(data)
        }

        // Check GIF
        if (isGif(data)) {
            return parseGif(data)
        }

        // Check BMP
        if (isBmp(data)) {
            return parseBmp(data)
        }

        // Check JPG
        if (isJpeg(data)) {
            return parseJpeg(data)
        }

        // Check WEBP (RIFF "WEBP")
        if (isWebp(data)) {
            return parseWebp(data)
        }

        // Check TIFF
        if (isTiff(data)) {
            return parseTiff(data)
        }

        return null
    }

    // ---------- Signatures ----------
    private fun isPng(b: ByteArray): Boolean {
        val pngSig = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        )
        if (b.size < pngSig.size) return false
        return pngSig.indices.all { b[it] == pngSig[it] }
    }

    private fun isGif(b: ByteArray): Boolean {
        if (b.size < 6) return false
        val s = b.decodeToString(0, 6)
        return s == "GIF87a" || s == "GIF89a"
    }

    private fun isBmp(b: ByteArray): Boolean {
        if (b.size < 2) return false
        return b[0] == 'B'.code.toByte() && b[1] == 'M'.code.toByte()
    }

    private fun isJpeg(b: ByteArray): Boolean {
        return b.size >= 2 && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte()
    }

    private fun isWebp(b: ByteArray): Boolean {
        if (b.size < 12) return false
        // "RIFF" + size(4) + "WEBP"
        return b[0] == 'R'.code.toByte() && b[1] == 'I'.code.toByte() &&
                b[2] == 'F'.code.toByte() && b[3] == 'F'.code.toByte() &&
                b[8] == 'W'.code.toByte() && b[9] == 'E'.code.toByte() &&
                b[10] == 'B'.code.toByte() && b[11] == 'P'.code.toByte()
    }

    private fun isTiff(b: ByteArray): Boolean {
        if (b.size < 4) return false
        // II (little endian) or MM (big endian), followed by 42 (0x002A)
        val ii = b[0] == 'I'.code.toByte() && b[1] == 'I'.code.toByte() &&
                b[2] == 0x2A.toByte() && b[3] == 0x00.toByte()
        val mm = b[0] == 'M'.code.toByte() && b[1] == 'M'.code.toByte() &&
                b[2] == 0x00.toByte() && b[3] == 0x2A.toByte()
        return ii || mm
    }

    // ---------- Parsers ----------
    private fun parsePng(b: ByteArray): ImageInfo? {
        // IHDR chunk starts at index 12 (length at 8-11, type at 12-15)
        if (b.size < 24) return null
        // confirm IHDR
        val ihdrType = b.decodeToString(12, 12 + 4)
        if (ihdrType != "IHDR") return null
        val width = readIntBigEndian(b, 16)
        val height = readIntBigEndian(b, 20)
        return ImageInfo(ImageFormat.PNG, width, height)
    }

    private fun parseGif(b: ByteArray): ImageInfo? {
        if (b.size < 10) return null
        val width = readShortLittleEndian(b, 6).toInt() and 0xFFFF
        val height = readShortLittleEndian(b, 8).toInt() and 0xFFFF
        return ImageInfo(ImageFormat.GIF, width, height)
    }

    private fun parseBmp(b: ByteArray): ImageInfo? {
        if (b.size < 26) return null
        // DIB header starts at offset 14. Commonly BITMAPINFOHEADER (size 40).
        val dibSize = readIntLittleEndian(b, 14)
        // For BITMAPINFOHEADER, width at offset 18, height at 22 (both 4 bytes, little-endian).
        if (b.size >= 18 + 8) {
            val width = readIntLittleEndian(b, 18)
            val heightRaw = readIntLittleEndian(b, 22)
            val height = if (heightRaw < 0) -heightRaw else heightRaw // top-down negative means top-down; return abs
            return ImageInfo(ImageFormat.BMP, width, height)
        }
        return null
    }

    private fun parseJpeg(b: ByteArray): ImageInfo? {
        var offset = 2
        val len = b.size
        // Iterate over JPEG markers to find SOF0/1/2 etc that contain width/height
        while (offset + 1 < len) {
            if (b[offset] != 0xFF.toByte()) {
                // Not a marker; corrupt
                return null
            }
            var marker = b[offset + 1].toInt() and 0xFF
            offset += 2
            // Skip padding 0xFF bytes
            while (marker == 0xFF) {
                if (offset >= len) return null
                marker = b[offset].toInt() and 0xFF
                offset++
            }

            // Markers without length: 0xD0..0xD9 (RST, SOI, EOI) but we handle length-markers
            if (marker == 0xD8 || marker == 0x01) {
                continue
            }
            if (marker == 0xD9) break // EOI
            if (offset + 1 >= len) return null
            val blockLength = readShortBigEndian(b, offset) // includes the length bytes
            if (blockLength < 2) return null
            val dataStart = offset + 2
            if (dataStart + blockLength - 2 > len) return null

            // SOF markers that contain width/height: 0xC0..0xC3,0xC5..0xC7,0xC9..0xCB,0xCD..0xCF
            if (marker in setOf(0xC0,0xC1,0xC2,0xC3,0xC5,0xC6,0xC7,0xC9,0xCA,0xCB,0xCD,0xCE,0xCF)) {
                if (dataStart + 5 >= len) return null
                val precision = b[dataStart].toInt() and 0xFF
                val height = readShortBigEndian(b, dataStart + 1)
                val width = readShortBigEndian(b, dataStart + 3)
                return ImageInfo(ImageFormat.JPEG, width, height)
            }

            offset = dataStart + blockLength - 2
        }
        return null
    }

    private fun parseWebp(b: ByteArray): ImageInfo? {
        // RIFF header validated earlier. Chunks start at offset 12.
        var offset = 12
        val len = b.size
        while (offset + 8 <= len) {
            val chunkId = b.decodeToString(offset, offset + 4)
            val chunkSize = readIntLittleEndian(b, offset + 4)
            val dataStart = offset + 8
            if (dataStart + chunkSize > len) return null

            when (chunkId) {
                "VP8X" -> {
                    // Extended: canvas size stored as 3 bytes little-endian each (minus 1)
                    if (chunkSize >= 10 && dataStart + 10 <= len) {
                        // width at bytes 4..6 of chunk data, height at 7..9
                        val w = read3BytesLittleEndian(b, dataStart + 4) + 1
                        val h = read3BytesLittleEndian(b, dataStart + 7) + 1
                        return ImageInfo(ImageFormat.WEBP, w, h)
                    }
                }
                "VP8L" -> {
                    // Lossless: first byte signature (should be 0x2F), next 4 bytes contain dims
                    if (chunkSize >= 5 && dataStart + 5 <= len) {
                        val signature = b[dataStart].toInt() and 0xFF
                        if (signature != 0x2F) {
                            // still try parsing but spec expects 0x2F
                        }
                        val b1 = b[dataStart + 1].toInt() and 0xFF
                        val b2 = b[dataStart + 2].toInt() and 0xFF
                        val b3 = b[dataStart + 3].toInt() and 0xFF
                        val b4 = b[dataStart + 4].toInt() and 0xFF
                        val width = 1 + (b1 or ((b2 and 0x3F) shl 8))
                        val height = 1 + (((b2 and 0xC0) shr 6) or (b3 shl 2) or ((b4 and 0x0F) shl 10))
                        return ImageInfo(ImageFormat.WEBP, width, height)
                    }
                }
                "VP8 " -> {
                    // Lossy: need to find start code 0x9d 0x01 0x2a inside chunk data and read 16-bit LE width/height
                    if (chunkSize >= 10) {
                        val end = dataStart + chunkSize
                        var p = dataStart
                        while (p + 9 <= end) {
                            if (b[p] == 0x9d.toByte() && b[p+1] == 0x01.toByte() && b[p+2] == 0x2a.toByte()) {
                                val width = readShortLittleEndian(b, p + 3) and 0x3FFF
                                val height = readShortLittleEndian(b, p + 5) and 0x3FFF
                                return ImageInfo(ImageFormat.WEBP, width, height)
                            }
                            p++
                        }
                        // If not found, try fallback: take 2 bytes at dataStart+6 and +8 (less reliable)
                        if (dataStart + 10 <= len) {
                            val w = readShortLittleEndian(b, dataStart + 6) and 0x3FFF
                            val h = readShortLittleEndian(b, dataStart + 8) and 0x3FFF
                            if (w > 0 && h > 0) return ImageInfo(ImageFormat.WEBP, w, h)
                        }
                    }
                }
            }

            // chunk sizes are padded to even bytes
            var advance = 8 + chunkSize
            if (chunkSize % 2 == 1) advance++ // pad byte
            offset += advance
        }
        return null
    }

    private fun parseTiff(b: ByteArray): ImageInfo? {
        if (b.size < 8) return null
        val littleEndian = b[0] == 'I'.code.toByte() && b[1] == 'I'.code.toByte()
        // magic 42 check done earlier
        val ifdOffset = if (littleEndian) readIntLittleEndian(b, 4) else readIntBigEndian(b, 4)
        if (ifdOffset < 0 || ifdOffset >= b.size) return null

        // read entries count (2 bytes) at ifdOffset
        if (ifdOffset + 2 > b.size) return null
        val numEntries = if (littleEndian) readShortLittleEndian(b, ifdOffset).toInt() and 0xFFFF
        else readShortBigEndian(b, ifdOffset).toInt() and 0xFFFF
        var entryOffset = ifdOffset + 2
        var width: Int? = null
        var height: Int? = null

        for (i in 0 until numEntries) {
            if (entryOffset + 12 > b.size) break
            val tag = if (littleEndian) readShortLittleEndian(b, entryOffset).toInt() and 0xFFFF
            else readShortBigEndian(b, entryOffset).toInt() and 0xFFFF
            val type = if (littleEndian) readShortLittleEndian(b, entryOffset + 2).toInt() and 0xFFFF
            else readShortBigEndian(b, entryOffset + 2).toInt() and 0xFFFF
            val count = if (littleEndian) readIntLittleEndian(b, entryOffset + 4)
            else readIntBigEndian(b, entryOffset + 4)
            val valueOffset = if (littleEndian) readIntLittleEndian(b, entryOffset + 8)
            else readIntBigEndian(b, entryOffset + 8)

            when (tag) {
                256 -> { // ImageWidth
                    width = readTiffValueAsInt(b, littleEndian, type, count, valueOffset, entryOffset + 8)
                }
                257 -> { // ImageLength (height)
                    height = readTiffValueAsInt(b, littleEndian, type, count, valueOffset, entryOffset + 8)
                }
            }
            if (width != null && height != null) break
            entryOffset += 12
        }

        if (width != null && height != null) {
            return ImageInfo(ImageFormat.TIFF, width, height)
        }
        return null
    }

    // ---------- TIFF value helper ----------
    // type: 1=BYTE,2=ASCII,3=SHORT (2 bytes),4=LONG (4 bytes),5=RATIONAL (8 bytes)
    private fun readTiffValueAsInt(
        b: ByteArray, littleEndian: Boolean, type: Int, count: Int, valueOffset: Int, inlineOffset: Int
    ): Int? {
        // If the value fits in 4 bytes, TIFF stores it in the "valueOffset" field directly.
        // For simplicity, handle common cases: SHORT, LONG, and first value of others.
        fun readFrom(pos: Int, size: Int): Int? {
            if (pos < 0 || pos + size > b.size) return null
            return when {
                size == 1 -> b[pos].toInt() and 0xFF
                size == 2 -> if (littleEndian) readShortLittleEndian(b, pos).toInt() and 0xFFFF
                else readShortBigEndian(b, pos).toInt() and 0xFFFF
                size == 4 -> if (littleEndian) readIntLittleEndian(b, pos)
                else readIntBigEndian(b, pos)
                else -> null
            }
        }

        when (type) {
            3 -> { // SHORT (2 bytes)
                // If count == 1 and valueOffset field stores it directly (in low 2 bytes)
                if (count >= 1) {
                    if (count == 1) {
                        // valueOffset contains the value possibly in low 2 bytes depending on endian
                        return if (littleEndian) (valueOffset and 0xFFFF) else ((valueOffset ushr 16) and 0xFFFF)
                    } else {
                        // value stored at offset
                        val pos = valueOffset
                        return readFrom(pos, 2)
                    }
                }
            }
            4 -> { // LONG (4 bytes)
                if (count >= 1) {
                    if (count == 1) {
                        return valueOffset
                    } else {
                        val pos = valueOffset
                        return readFrom(pos, 4)
                    }
                }
            }
            1 -> { // BYTE
                val pos = if (count == 1) inlineOffset else valueOffset
                return readFrom(pos, 1)
            }
            5 -> { // RATIONAL (two LONGs) -> take first LONG numerator / denominator - but we only need integer
                val pos = valueOffset
                val num = readFrom(pos, 4) ?: return null
                val den = readFrom(pos + 4, 4) ?: return null
                if (den == 0) return null
                return num / den
            }
            else -> {
                // Fallback: attempt to read 4-byte value at valueOffset
                return if (valueOffset >= 0 && valueOffset + 4 <= b.size) {
                    if (littleEndian) readIntLittleEndian(b, valueOffset) else readIntBigEndian(b, valueOffset)
                } else null
            }
        }
        return null
    }

    // ---------- Byte read helpers ----------
    private fun readShortBigEndian(b: ByteArray, offset: Int): Int {
        if (offset + 1 >= b.size) return 0
        return ((b[offset].toInt() and 0xFF) shl 8) or (b[offset + 1].toInt() and 0xFF)
    }

    private fun readShortLittleEndian(b: ByteArray, offset: Int): Int {
        if (offset + 1 >= b.size) return 0
        return ((b[offset + 1].toInt() and 0xFF) shl 8) or (b[offset].toInt() and 0xFF)
    }

    private fun readIntBigEndian(b: ByteArray, offset: Int): Int {
        if (offset + 3 >= b.size) return 0
        return ((b[offset].toInt() and 0xFF) shl 24) or
                ((b[offset + 1].toInt() and 0xFF) shl 16) or
                ((b[offset + 2].toInt() and 0xFF) shl 8) or
                (b[offset + 3].toInt() and 0xFF)
    }

    private fun readIntLittleEndian(b: ByteArray, offset: Int): Int {
        if (offset + 3 >= b.size) return 0
        return ((b[offset + 3].toInt() and 0xFF) shl 24) or
                ((b[offset + 2].toInt() and 0xFF) shl 16) or
                ((b[offset + 1].toInt() and 0xFF) shl 8) or
                (b[offset].toInt() and 0xFF)
    }

    private fun read3BytesLittleEndian(b: ByteArray, offset: Int): Int {
        if (offset + 2 >= b.size) return 0
        return (b[offset].toInt() and 0xFF) or
                ((b[offset + 1].toInt() and 0xFF) shl 8) or
                ((b[offset + 2].toInt() and 0xFF) shl 16)
    }
}