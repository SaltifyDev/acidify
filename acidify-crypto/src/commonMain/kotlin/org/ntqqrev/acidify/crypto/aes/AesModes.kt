@file:JvmName("GCMKt")

package org.ntqqrev.acidify.crypto.aes

import kotlin.experimental.xor
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.random.Random

/**
 * AES modes of operation (ECB, CBC, CTR, GCM).
 * Direct translation from aes_modes.hpp
 */

// Padding schemes
enum class PaddingScheme {
    NONE,     // No padding (data must be block-aligned)
    PKCS7,    // PKCS#7 padding
    ZEROS,    // Zero padding
    ISO7816   // ISO/IEC 7816-4 padding
}

// Apply padding to buffer
fun applyPadding(buffer: ByteArray, dataLen: Int, scheme: PaddingScheme): Int {
    if (scheme == PaddingScheme.NONE) {
        if (dataLen % AesCore.AES_BLOCK_SIZE != 0) {
            throw IllegalArgumentException("Data length must be multiple of block size with no padding")
        }
        return dataLen
    }

    var paddingLen = AesCore.AES_BLOCK_SIZE - (dataLen % AesCore.AES_BLOCK_SIZE)
    if (paddingLen == 0) paddingLen = AesCore.AES_BLOCK_SIZE  // Always add padding

    if (dataLen + paddingLen > buffer.size) {
        throw IllegalArgumentException("Buffer too small for padding")
    }

    when (scheme) {
        PaddingScheme.PKCS7 -> {
            for (i in dataLen until dataLen + paddingLen) {
                buffer[i] = paddingLen.toByte()
            }
        }

        PaddingScheme.ZEROS -> {
            for (i in dataLen until dataLen + paddingLen) {
                buffer[i] = 0
            }
        }

        PaddingScheme.ISO7816 -> {
            buffer[dataLen] = 0x80.toByte()
            if (paddingLen > 1) {
                for (i in dataLen + 1 until dataLen + paddingLen) {
                    buffer[i] = 0
                }
            }
        }

        else -> {}
    }

    return dataLen + paddingLen
}

// Remove padding from buffer
fun removePadding(buffer: ByteArray, dataLen: Int, scheme: PaddingScheme): Int {
    if (scheme == PaddingScheme.NONE || dataLen == 0) {
        return dataLen
    }

    if (dataLen % AesCore.AES_BLOCK_SIZE != 0) {
        throw IllegalArgumentException("Invalid padded data length")
    }

    return when (scheme) {
        PaddingScheme.PKCS7 -> {
            val paddingLen = buffer[dataLen - 1].toInt() and 0xFF
            if (paddingLen == 0 || paddingLen > AesCore.AES_BLOCK_SIZE) {
                throw IllegalArgumentException("Invalid PKCS7 padding")
            }
            // Verify all padding bytes
            for (i in dataLen - paddingLen until dataLen) {
                if ((buffer[i].toInt() and 0xFF) != paddingLen) {
                    throw IllegalArgumentException("Invalid PKCS7 padding")
                }
            }
            dataLen - paddingLen
        }

        PaddingScheme.ZEROS -> {
            // Remove trailing zeros
            var newLen = dataLen
            while (newLen > 0 && buffer[newLen - 1] == 0.toByte()) {
                newLen--
            }
            newLen
        }

        PaddingScheme.ISO7816 -> {
            // Find 0x80 byte from the end
            for (i in dataLen - 1 downTo 0) {
                if (buffer[i] == 0x80.toByte()) {
                    return i
                } else if (buffer[i] != 0.toByte()) {
                    throw IllegalArgumentException("Invalid ISO7816 padding")
                }
            }
            throw IllegalArgumentException("Invalid ISO7816 padding")
        }

        else -> dataLen
    }
}

// ECB (Electronic Codebook) mode
class ECB(key: ByteArray, private val paddingScheme: PaddingScheme = PaddingScheme.PKCS7) {
    private val keySchedule = AesCore.KeySchedule()

    init {
        keySchedule.expandKey(key)
    }

    fun encrypt(plaintext: ByteArray): ByteArray {
        val result = ByteArray(plaintext.size + AesCore.AES_BLOCK_SIZE)
        plaintext.copyInto(result, 0, 0, plaintext.size)

        val paddedLen = applyPadding(result, plaintext.size, paddingScheme)

        // Encrypt each block independently
        for (i in 0 until paddedLen step AesCore.AES_BLOCK_SIZE) {
            AesCore.encryptBlock(result, i, result, i, keySchedule)
        }

        return result.sliceArray(0 until paddedLen)
    }

    fun decrypt(ciphertext: ByteArray): ByteArray {
        if (ciphertext.size % AesCore.AES_BLOCK_SIZE != 0) {
            throw IllegalArgumentException("Invalid ciphertext length for ECB mode")
        }

        val result = ByteArray(ciphertext.size)

        // Decrypt each block independently
        for (i in 0 until ciphertext.size step AesCore.AES_BLOCK_SIZE) {
            AesCore.decryptBlock(ciphertext, i, result, i, keySchedule)
        }

        val unpaddedLen = removePadding(result, result.size, paddingScheme)
        return result.sliceArray(0 until unpaddedLen)
    }
}

// CBC (Cipher Block Chaining) mode
class CBC(
    key: ByteArray,
    iv: ByteArray,
    private val paddingScheme: PaddingScheme = PaddingScheme.PKCS7
) {
    private val keySchedule = AesCore.KeySchedule()
    private val iv = ByteArray(AesCore.AES_BLOCK_SIZE)

    init {
        if (iv.size != AesCore.AES_BLOCK_SIZE) {
            throw IllegalArgumentException("IV must be 16 bytes for CBC mode")
        }
        keySchedule.expandKey(key)
        iv.copyInto(this.iv, 0, 0, AesCore.AES_BLOCK_SIZE)
    }

    fun encrypt(plaintext: ByteArray): ByteArray {
        val result = ByteArray(plaintext.size + AesCore.AES_BLOCK_SIZE)
        plaintext.copyInto(result, 0, 0, plaintext.size)

        val paddedLen = applyPadding(result, plaintext.size, paddingScheme)

        val prevBlock = ByteArray(AesCore.AES_BLOCK_SIZE)
        iv.copyInto(prevBlock, 0, 0, AesCore.AES_BLOCK_SIZE)

        // Encrypt with block chaining
        for (i in 0 until paddedLen step AesCore.AES_BLOCK_SIZE) {
            // XOR with previous ciphertext block
            for (j in 0 until AesCore.AES_BLOCK_SIZE) {
                result[i + j] = result[i + j] xor prevBlock[j]
            }

            AesCore.encryptBlock(result, i, result, i, keySchedule)
            result.copyInto(prevBlock, 0, i, i + AesCore.AES_BLOCK_SIZE)
        }

        return result.sliceArray(0 until paddedLen)
    }

    fun decrypt(ciphertext: ByteArray): ByteArray {
        if (ciphertext.size % AesCore.AES_BLOCK_SIZE != 0) {
            throw IllegalArgumentException("Invalid ciphertext length for CBC mode")
        }

        val result = ByteArray(ciphertext.size)

        val prevBlock = ByteArray(AesCore.AES_BLOCK_SIZE)
        iv.copyInto(prevBlock, 0, 0, AesCore.AES_BLOCK_SIZE)

        // Decrypt with block chaining
        for (i in 0 until ciphertext.size step AesCore.AES_BLOCK_SIZE) {
            AesCore.decryptBlock(ciphertext, i, result, i, keySchedule)

            // XOR with previous ciphertext block
            for (j in 0 until AesCore.AES_BLOCK_SIZE) {
                result[i + j] = result[i + j] xor prevBlock[j]
            }

            ciphertext.copyInto(prevBlock, 0, i, i + AesCore.AES_BLOCK_SIZE)
        }

        val unpaddedLen = removePadding(result, result.size, paddingScheme)
        return result.sliceArray(0 until unpaddedLen)
    }

    fun resetIv(iv: ByteArray) {
        if (iv.size != AesCore.AES_BLOCK_SIZE) {
            throw IllegalArgumentException("IV must be 16 bytes")
        }
        iv.copyInto(this.iv, 0, 0, AesCore.AES_BLOCK_SIZE)
    }
}

// CTR (Counter) mode
class CTR(key: ByteArray, nonce: ByteArray) {
    private val keySchedule = AesCore.KeySchedule()
    private val counter = ByteArray(AesCore.AES_BLOCK_SIZE)
    private val keystream = ByteArray(AesCore.AES_BLOCK_SIZE)
    private var keystreamPos = AesCore.AES_BLOCK_SIZE

    init {
        if (nonce.size > AesCore.AES_BLOCK_SIZE) {
            throw IllegalArgumentException("Nonce too large for CTR mode")
        }

        keySchedule.expandKey(key)

        // Initialize counter with nonce
        counter.fill(0)
        nonce.copyInto(counter, 0, 0, nonce.size)

        // If nonce is less than 16 bytes, use remaining bytes as counter
        // Counter starts at 0 in the remaining bytes
    }

    private fun generateKeystream() {
        AesCore.encryptBlock(counter, 0, keystream, 0, keySchedule)
        GcmHelper.incrementCounter(counter)
        keystreamPos = 0
    }

    fun encrypt(plaintext: ByteArray): ByteArray {
        return process(plaintext)
    }

    fun decrypt(ciphertext: ByteArray): ByteArray {
        return process(ciphertext)  // CTR mode encryption and decryption are the same
    }

    fun processBytes(input: ByteArray, inputOffset: Int, output: ByteArray, outputOffset: Int, len: Int) {
        var remainingLen = len
        var currentInputOffset = inputOffset
        var currentOutputOffset = outputOffset

        while (remainingLen > 0) {
            if (keystreamPos >= AesCore.AES_BLOCK_SIZE) {
                generateKeystream()
            }

            val chunk = minOf(remainingLen, AesCore.AES_BLOCK_SIZE - keystreamPos)
            for (i in 0 until chunk) {
                output[currentOutputOffset + i] = input[currentInputOffset + i] xor keystream[keystreamPos + i]
            }

            currentInputOffset += chunk
            currentOutputOffset += chunk
            remainingLen -= chunk
            keystreamPos += chunk
        }
    }

    fun resetCounter(nonce: ByteArray) {
        if (nonce.size > AesCore.AES_BLOCK_SIZE) {
            throw IllegalArgumentException("Nonce too large")
        }
        counter.fill(0)
        nonce.copyInto(counter, 0, 0, nonce.size)
        keystreamPos = AesCore.AES_BLOCK_SIZE  // Force regeneration
    }

    private fun process(data: ByteArray): ByteArray {
        val result = ByteArray(data.size)
        processBytes(data, 0, result, 0, data.size)
        return result
    }
}

// GCM (Galois/Counter Mode) - Authenticated encryption
class GCM(key: ByteArray, private val tagSize: GcmHelper.TagSize = GcmHelper.TagSize.TAG_128) {
    private val ctx = GcmHelper.GCMContext()

    init {
        ctx.init(key)
    }

    data class EncryptResult(
        val ciphertext: ByteArray,
        val tag: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EncryptResult) return false
            return ciphertext.contentEquals(other.ciphertext) && tag.contentEquals(other.tag)
        }

        override fun hashCode(): Int {
            return 31 * ciphertext.contentHashCode() + tag.contentHashCode()
        }
    }

    fun encrypt(plaintext: ByteArray, iv: ByteArray, aad: ByteArray = ByteArray(0)): EncryptResult {
        ctx.start(iv)

        if (aad.isNotEmpty()) {
            ctx.updateAad(aad)
        }

        val ciphertext = ByteArray(plaintext.size)
        ctx.encryptUpdate(plaintext, 0, ciphertext, 0, plaintext.size)

        val tag = ByteArray(tagSize.bytes)
        ctx.finish(tag)

        return EncryptResult(ciphertext, tag)
    }

    fun decrypt(ciphertext: ByteArray, iv: ByteArray, tag: ByteArray, aad: ByteArray = ByteArray(0)): ByteArray {
        ctx.start(iv)

        if (aad.isNotEmpty()) {
            ctx.updateAad(aad)
        }

        val plaintext = ByteArray(ciphertext.size)
        ctx.decryptUpdate(ciphertext, 0, plaintext, 0, ciphertext.size)

        if (!ctx.verify(tag)) {
            throw IllegalArgumentException("GCM authentication failed")
        }

        return plaintext
    }

    companion object {
        // One-shot authenticated encryption
        @JvmStatic
        fun encryptAuthenticated(
            key: ByteArray,
            plaintext: ByteArray,
            iv: ByteArray,
            aad: ByteArray = ByteArray(0),
            tagSize: GcmHelper.TagSize = GcmHelper.TagSize.TAG_128
        ): EncryptResult {
            val gcm = GCM(key, tagSize)
            return gcm.encrypt(plaintext, iv, aad)
        }

        // One-shot authenticated decryption
        @JvmStatic
        fun decryptAuthenticated(
            key: ByteArray,
            ciphertext: ByteArray,
            iv: ByteArray,
            tag: ByteArray,
            aad: ByteArray = ByteArray(0)
        ): ByteArray {
            val gcm = GCM(key, GcmHelper.TagSize.entries.find { it.bytes == tag.size } ?: GcmHelper.TagSize.TAG_128)
            return gcm.decrypt(ciphertext, iv, tag, aad)
        }
    }
}

// Generate random IV/nonce
fun generateRandomIv(size: Int = AesCore.AES_BLOCK_SIZE): ByteArray {
    return Random.nextBytes(size)
}

// Helper function to create AES cipher with automatic mode detection
inline fun <reified T> createCipher(key: ByteArray, vararg args: Any): T where T : Any {
    return when (T::class) {
        ECB::class -> ECB(key, args.getOrNull(0) as? PaddingScheme ?: PaddingScheme.PKCS7) as T
        CBC::class -> CBC(key, args[0] as ByteArray, args.getOrNull(1) as? PaddingScheme ?: PaddingScheme.PKCS7) as T
        CTR::class -> CTR(key, args[0] as ByteArray) as T
        GCM::class -> GCM(key, args.getOrNull(0) as? GcmHelper.TagSize ?: GcmHelper.TagSize.TAG_128) as T
        else -> throw IllegalArgumentException("Unknown cipher mode")
    }
}