package org.lagrange.library.crypto.aes

import kotlin.jvm.JvmStatic
import kotlin.random.Random

/**
 * AES algorithm interface combining all modes.
 * Direct translation from aes.hpp
 */
class AES {

    // Key size enum
    enum class KeySize(val bytes: Int) {
        AES128(16),  // 128 bits
        AES192(24),  // 192 bits
        AES256(32)   // 256 bits
    }

    // Mode of operation
    enum class Mode {
        ECB,
        CBC,
        CTR,
        GCM
    }

    companion object {
        // Static factory methods for each mode
        @JvmStatic
        fun createEcb(key: ByteArray, padding: PaddingScheme = PaddingScheme.PKCS7): ECB {
            return ECB(key, padding)
        }

        @JvmStatic
        fun createCbc(key: ByteArray, iv: ByteArray, padding: PaddingScheme = PaddingScheme.PKCS7): CBC {
            return CBC(key, iv, padding)
        }

        @JvmStatic
        fun createCtr(key: ByteArray, nonce: ByteArray): CTR {
            return CTR(key, nonce)
        }

        @JvmStatic
        fun createGcm(key: ByteArray, tagSize: GcmHelper.TagSize = GcmHelper.TagSize.TAG_128): GCM {
            return GCM(key, tagSize)
        }

        // Convenience methods for string encryption/decryption
        @JvmStatic
        fun encryptString(
            mode: Mode,
            plaintext: String,
            key: ByteArray,
            ivOrNonce: ByteArray = ByteArray(0),
            padding: PaddingScheme = PaddingScheme.PKCS7
        ): ByteArray {
            val plaintextBytes = plaintext.encodeToByteArray()

            return when (mode) {
                Mode.ECB -> {
                    val cipher = ECB(key, padding)
                    cipher.encrypt(plaintextBytes)
                }

                Mode.CBC -> {
                    if (ivOrNonce.size != AesCore.AES_BLOCK_SIZE) {
                        throw IllegalArgumentException("CBC mode requires 16-byte IV")
                    }
                    val cipher = CBC(key, ivOrNonce, padding)
                    cipher.encrypt(plaintextBytes)
                }

                Mode.CTR -> {
                    val cipher = CTR(key, ivOrNonce)
                    cipher.encrypt(plaintextBytes)
                }

                Mode.GCM -> {
                    val cipher = GCM(key)
                    val result = cipher.encrypt(plaintextBytes, ivOrNonce)
                    // Append tag to ciphertext for convenience
                    result.ciphertext + result.tag
                }
            }
        }

        @JvmStatic
        fun decryptToString(
            mode: Mode,
            ciphertext: ByteArray,
            key: ByteArray,
            ivOrNonce: ByteArray = ByteArray(0),
            padding: PaddingScheme = PaddingScheme.PKCS7
        ): String {
            val plaintextBytes = when (mode) {
                Mode.ECB -> {
                    val cipher = ECB(key, padding)
                    cipher.decrypt(ciphertext)
                }

                Mode.CBC -> {
                    if (ivOrNonce.size != AesCore.AES_BLOCK_SIZE) {
                        throw IllegalArgumentException("CBC mode requires 16-byte IV")
                    }
                    val cipher = CBC(key, ivOrNonce, padding)
                    cipher.decrypt(ciphertext)
                }

                Mode.CTR -> {
                    val cipher = CTR(key, ivOrNonce)
                    cipher.decrypt(ciphertext)
                }

                Mode.GCM -> {
                    if (ciphertext.size < 16) {
                        throw IllegalArgumentException("GCM ciphertext too short")
                    }
                    // Assume last 16 bytes are the tag
                    val ct = ciphertext.sliceArray(0 until ciphertext.size - 16)
                    val tag = ciphertext.sliceArray(ciphertext.size - 16 until ciphertext.size)
                    val cipher = GCM(key)
                    cipher.decrypt(ct, ivOrNonce, tag)
                }
            }

            return plaintextBytes.decodeToString()
        }

        // Utility functions
        @JvmStatic
        fun getKeySize(size: KeySize): Int {
            return size.bytes
        }

        @JvmStatic
        fun getBlockSize(): Int {
            return AesCore.AES_BLOCK_SIZE
        }

        @JvmStatic
        fun isHardwareAccelerated(): Boolean {
            return AesCore.hasAesNi()
        }

        // Key validation
        @JvmStatic
        fun isValidKeySize(size: Int): Boolean {
            return size == 16 || size == 24 || size == 32
        }

        // Generate random key
        @JvmStatic
        fun generateKey(size: KeySize): ByteArray {
            return Random.nextBytes(size.bytes)
        }
    }
}

// Convenience type aliases
typealias AES128 = AES  // All modes support all key sizes
typealias AES192 = AES
typealias AES256 = AES

// Mode-specific type aliases for clarity
typealias AES_ECB = ECB
typealias AES_CBC = CBC
typealias AES_CTR = CTR
typealias AES_GCM = GCM

// Helper functions for common operations

// Encrypt data with AES-GCM (recommended for most use cases)
fun aesGcmEncrypt(
    key: ByteArray,
    plaintext: ByteArray,
    iv: ByteArray,
    aad: ByteArray = ByteArray(0),
    tagSize: GcmHelper.TagSize = GcmHelper.TagSize.TAG_128
): GCM.EncryptResult {
    return GCM.encryptAuthenticated(key, plaintext, iv, aad, tagSize)
}

// Decrypt data with AES-GCM
fun aesGcmDecrypt(
    key: ByteArray,
    ciphertext: ByteArray,
    iv: ByteArray,
    tag: ByteArray,
    aad: ByteArray = ByteArray(0)
): ByteArray {
    return GCM.decryptAuthenticated(key, ciphertext, iv, tag, aad)
}

// Simple CTR mode encryption (streaming cipher)
fun aesCtrEncrypt(
    key: ByteArray,
    plaintext: ByteArray,
    nonce: ByteArray
): ByteArray {
    val cipher = CTR(key, nonce)
    return cipher.encrypt(plaintext)
}

// Simple CTR mode decryption
fun aesCtrDecrypt(
    key: ByteArray,
    ciphertext: ByteArray,
    nonce: ByteArray
): ByteArray {
    val cipher = CTR(key, nonce)
    return cipher.decrypt(ciphertext)
}

// CBC mode with automatic IV generation
fun aesCbcEncryptWithIv(
    key: ByteArray,
    plaintext: ByteArray,
    padding: PaddingScheme = PaddingScheme.PKCS7
): Pair<ByteArray, ByteArray> {
    val iv = generateRandomIv()
    val cipher = CBC(key, iv, padding)
    val ciphertext = cipher.encrypt(plaintext)
    return Pair(ciphertext, iv)
}

// Hex string conversion utilities
fun bytesToHex(bytes: ByteArray): String {
    val hexChars = "0123456789abcdef"
    val result = StringBuilder(bytes.size * 2)

    for (byte in bytes) {
        val value = byte.toInt() and 0xFF
        result.append(hexChars[value shr 4])
        result.append(hexChars[value and 0x0f])
    }
    return result.toString()
}

fun hexToBytes(hex: String): ByteArray {
    if (hex.length % 2 != 0) {
        throw IllegalArgumentException("Hex string must have even length")
    }

    val result = ByteArray(hex.length / 2)

    for (i in hex.indices step 2) {
        fun getNibble(c: Char): Int {
            return when (c) {
                in '0'..'9' -> c - '0'
                in 'a'..'f' -> c - 'a' + 10
                in 'A'..'F' -> c - 'A' + 10
                else -> throw IllegalArgumentException("Invalid hex character")
            }
        }

        val byte = (getNibble(hex[i]) shl 4) or getNibble(hex[i + 1])
        result[i / 2] = byte.toByte()
    }
    return result
}

// Base64 encoding/decoding utilities
fun bytesToBase64(bytes: ByteArray): String {
    val base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val result = StringBuilder(((bytes.size + 2) / 3) * 4)

    for (i in bytes.indices step 3) {
        val octetA = bytes[i].toInt() and 0xFF
        val octetB = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
        val octetC = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0

        val triple = (octetA shl 16) + (octetB shl 8) + octetC

        result.append(base64Chars[(triple shr 18) and 0x3f])
        result.append(base64Chars[(triple shr 12) and 0x3f])
        result.append(if (i + 1 < bytes.size) base64Chars[(triple shr 6) and 0x3f] else '=')
        result.append(if (i + 2 < bytes.size) base64Chars[triple and 0x3f] else '=')
    }

    return result.toString()
}

fun base64ToBytes(base64: String): ByteArray {
    val decodeTable = intArrayOf(
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64,
        64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64,
        64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64
    )

    if (base64.isEmpty() || base64.length % 4 != 0) {
        throw IllegalArgumentException("Invalid base64 string")
    }

    val result = mutableListOf<Byte>()

    for (i in base64.indices step 4) {
        val sextetA = if (base64[i].code < 128) decodeTable[base64[i].code] else 64
        val sextetB = if (base64[i + 1].code < 128) decodeTable[base64[i + 1].code] else 64
        val sextetC = if (base64[i + 2].code < 128) decodeTable[base64[i + 2].code] else 64
        val sextetD = if (base64[i + 3].code < 128) decodeTable[base64[i + 3].code] else 64

        if (sextetA == 64 || sextetB == 64) {
            throw IllegalArgumentException("Invalid base64 character")
        }

        val triple = (sextetA shl 18) + (sextetB shl 12) +
                (if (sextetC == 64) 0 else (sextetC shl 6)) +
                (if (sextetD == 64) 0 else sextetD)

        result.add(((triple shr 16) and 0xff).toByte())
        if (base64[i + 2] != '=') {
            result.add(((triple shr 8) and 0xff).toByte())
        }
        if (base64[i + 3] != '=') {
            result.add((triple and 0xff).toByte())
        }
    }

    return result.toByteArray()
}