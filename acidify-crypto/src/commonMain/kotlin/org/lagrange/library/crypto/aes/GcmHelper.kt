package org.lagrange.library.crypto.aes

import kotlin.experimental.xor

/**
 * GCM (Galois/Counter Mode) implementation for authenticated encryption.
 * Direct translation from gcm.hpp
 */
object GcmHelper {

    // GCM field size (128 bits)
    const val GCM_BLOCK_SIZE: Int = 16

    // GCM authentication tag size options
    enum class TagSize(val bytes: Int) {
        TAG_96(12),   // 96 bits
        TAG_104(13),  // 104 bits
        TAG_112(14),  // 112 bits
        TAG_120(15),  // 120 bits
        TAG_128(16)   // 128 bits (recommended)
    }

    // GF(2^128) multiplication for GHASH - corrected implementation
    class GHashMultiplier {
        private val H = ByteArray(16)  // Hash subkey

        fun setHashKey(hashKey: ByteArray) {
            hashKey.copyInto(H, 0, 0, 16)
        }

        // Multiply block by H in GF(2^128) - corrected algorithm
        fun multiply(block: ByteArray, offset: Int = 0) {
            // Convert to 64-bit words for efficient manipulation
            val X = LongArray(2)
            val Y = LongArray(2)
            val Z = LongArray(2)

            // Load block and H as big-endian 64-bit words
            X[0] = (block[offset + 0].toLong() and 0xFF shl 56) or
                    (block[offset + 1].toLong() and 0xFF shl 48) or
                    (block[offset + 2].toLong() and 0xFF shl 40) or
                    (block[offset + 3].toLong() and 0xFF shl 32) or
                    (block[offset + 4].toLong() and 0xFF shl 24) or
                    (block[offset + 5].toLong() and 0xFF shl 16) or
                    (block[offset + 6].toLong() and 0xFF shl 8) or
                    (block[offset + 7].toLong() and 0xFF)

            X[1] = (block[offset + 8].toLong() and 0xFF shl 56) or
                    (block[offset + 9].toLong() and 0xFF shl 48) or
                    (block[offset + 10].toLong() and 0xFF shl 40) or
                    (block[offset + 11].toLong() and 0xFF shl 32) or
                    (block[offset + 12].toLong() and 0xFF shl 24) or
                    (block[offset + 13].toLong() and 0xFF shl 16) or
                    (block[offset + 14].toLong() and 0xFF shl 8) or
                    (block[offset + 15].toLong() and 0xFF)

            Y[0] = (H[0].toLong() and 0xFF shl 56) or
                    (H[1].toLong() and 0xFF shl 48) or
                    (H[2].toLong() and 0xFF shl 40) or
                    (H[3].toLong() and 0xFF shl 32) or
                    (H[4].toLong() and 0xFF shl 24) or
                    (H[5].toLong() and 0xFF shl 16) or
                    (H[6].toLong() and 0xFF shl 8) or
                    (H[7].toLong() and 0xFF)

            Y[1] = (H[8].toLong() and 0xFF shl 56) or
                    (H[9].toLong() and 0xFF shl 48) or
                    (H[10].toLong() and 0xFF shl 40) or
                    (H[11].toLong() and 0xFF shl 32) or
                    (H[12].toLong() and 0xFF shl 24) or
                    (H[13].toLong() and 0xFF shl 16) or
                    (H[14].toLong() and 0xFF shl 8) or
                    (H[15].toLong() and 0xFF)

            // Multiplication algorithm
            for (i in 0 until 128) {
                // Check the i-th bit of X
                val wordIdx = i / 64
                val bitIdx = 63 - (i % 64)

                if ((X[wordIdx] shr bitIdx and 1L) != 0L) {
                    Z[0] = Z[0] xor Y[0]
                    Z[1] = Z[1] xor Y[1]
                }

                // Multiply Y by alpha (shift right by 1 bit)
                val lsb = (Y[1] and 1L) != 0L
                Y[1] = (Y[1] ushr 1) or ((Y[0] and 1L) shl 63)
                Y[0] = Y[0] ushr 1

                // If LSB was set, XOR with reduction polynomial
                if (lsb) {
                    Y[0] =
                        Y[0] xor -0x1F00000000000000L  // R = x^128 + x^7 + x^2 + x + 1 (0xE100000000000000 as signed)
                }
            }

            // Store result back as bytes
            block[offset + 0] = (Z[0] shr 56).toByte()
            block[offset + 1] = (Z[0] shr 48).toByte()
            block[offset + 2] = (Z[0] shr 40).toByte()
            block[offset + 3] = (Z[0] shr 32).toByte()
            block[offset + 4] = (Z[0] shr 24).toByte()
            block[offset + 5] = (Z[0] shr 16).toByte()
            block[offset + 6] = (Z[0] shr 8).toByte()
            block[offset + 7] = Z[0].toByte()
            block[offset + 8] = (Z[1] shr 56).toByte()
            block[offset + 9] = (Z[1] shr 48).toByte()
            block[offset + 10] = (Z[1] shr 40).toByte()
            block[offset + 11] = (Z[1] shr 32).toByte()
            block[offset + 12] = (Z[1] shr 24).toByte()
            block[offset + 13] = (Z[1] shr 16).toByte()
            block[offset + 14] = (Z[1] shr 8).toByte()
            block[offset + 15] = Z[1].toByte()
        }

        // Optimized GHASH for multiple blocks
        fun ghash(output: ByteArray, outputOffset: Int, input: ByteArray, inputOffset: Int, len: Int) {
            var remainingLen = len
            var currentInputOffset = inputOffset

            while (remainingLen >= 16) {
                // Y = (Y XOR X_i) * H
                for (i in 0 until 16) {
                    output[outputOffset + i] = output[outputOffset + i] xor input[currentInputOffset + i]
                }
                multiply(output, outputOffset)

                currentInputOffset += 16
                remainingLen -= 16
            }

            // Handle partial block
            if (remainingLen > 0) {
                val partial = ByteArray(16)
                input.copyInto(partial, 0, currentInputOffset, currentInputOffset + remainingLen)
                for (i in 0 until 16) {
                    output[outputOffset + i] = output[outputOffset + i] xor partial[i]
                }
                multiply(output, outputOffset)
            }
        }
    }

    // Increment counter for CTR mode (big-endian)
    fun incrementCounter(counter: ByteArray, offset: Int = 0) {
        // Increment the last 4 bytes (32-bit counter) in big-endian order
        for (i in 15 downTo 12) {
            if (++counter[offset + i] != 0.toByte()) break
        }
    }

    // Increment counter by specific amount
    fun incrementCounterBy(counter: ByteArray, offset: Int, amount: Int) {
        // Convert last 4 bytes to uint32_t (big-endian)
        var ctr = (counter[offset + 12].toInt() and 0xFF shl 24) or
                (counter[offset + 13].toInt() and 0xFF shl 16) or
                (counter[offset + 14].toInt() and 0xFF shl 8) or
                (counter[offset + 15].toInt() and 0xFF)

        ctr += amount

        // Convert back to big-endian bytes
        counter[offset + 12] = (ctr shr 24).toByte()
        counter[offset + 13] = (ctr shr 16).toByte()
        counter[offset + 14] = (ctr shr 8).toByte()
        counter[offset + 15] = ctr.toByte()
    }

    // GCTR function (CTR mode encryption for GCM)
    fun gctr(
        input: ByteArray,
        inputOffset: Int,
        output: ByteArray,
        outputOffset: Int,
        len: Int,
        icb: ByteArray,
        keySchedule: AesCore.KeySchedule
    ) {
        if (len == 0) return

        val counter = ByteArray(16)
        icb.copyInto(counter, 0, 0, 16)

        val keystream = ByteArray(16)
        var remainingLen = len
        var currentInputOffset = inputOffset
        var currentOutputOffset = outputOffset

        while (remainingLen >= 16) {
            AesCore.encryptBlock(counter, 0, keystream, 0, keySchedule)
            for (i in 0 until 16) {
                output[currentOutputOffset + i] = input[currentInputOffset + i] xor keystream[i]
            }
            incrementCounter(counter)
            currentInputOffset += 16
            currentOutputOffset += 16
            remainingLen -= 16
        }

        // Handle partial block
        if (remainingLen > 0) {
            AesCore.encryptBlock(counter, 0, keystream, 0, keySchedule)
            for (i in 0 until remainingLen) {
                output[currentOutputOffset + i] = input[currentInputOffset + i] xor keystream[i]
            }
        }
    }

    // GCM context for authenticated encryption
    class GCMContext {
        private val keySchedule = AesCore.KeySchedule()
        private val ghash = GHashMultiplier()
        private val H = ByteArray(16)      // Hash subkey
        private val J0 = ByteArray(16)     // Pre-counter block
        private val counter = ByteArray(16) // Current counter
        private val authTag = ByteArray(16) // Authentication accumulator
        private var aadLen: Long = 0
        private var ctLen: Long = 0
        private var initialized = false

        fun init(key: ByteArray) {
            keySchedule.expandKey(key)

            // Generate hash subkey H = E(K, 0^128)
            H.fill(0)
            AesCore.encryptBlock(H, 0, H, 0, keySchedule)
            ghash.setHashKey(H)

            initialized = true
        }

        fun start(iv: ByteArray) {
            if (!initialized) return

            aadLen = 0
            ctLen = 0
            authTag.fill(0)

            if (iv.size == 12) {
                // If IV is 96 bits, J0 = IV || 0^31 || 1
                iv.copyInto(J0, 0, 0, 12)
                J0[12] = 0
                J0[13] = 0
                J0[14] = 0
                J0[15] = 1
            } else {
                // Otherwise, J0 = GHASH(IV || 0^s || len(IV))
                J0.fill(0)
                ghash.ghash(J0, 0, iv, 0, iv.size)

                // Append length block
                val lenBlock = ByteArray(16)
                val bitLen = iv.size.toLong() * 8
                // Store length in big-endian format in the last 8 bytes
                for (i in 7 downTo 0) {
                    lenBlock[15 - i] = (bitLen shr (i * 8)).toByte()
                }
                ghash.ghash(J0, 0, lenBlock, 0, 16)
            }

            // Set initial counter
            J0.copyInto(counter, 0, 0, 16)
            incrementCounter(counter)
        }

        fun updateAad(aad: ByteArray) {
            if (!initialized || aad.isEmpty()) return

            ghash.ghash(authTag, 0, aad, 0, aad.size)
            aadLen += aad.size
        }

        fun encryptUpdate(
            plaintext: ByteArray,
            plaintextOffset: Int,
            ciphertext: ByteArray,
            ciphertextOffset: Int,
            len: Int
        ) {
            if (!initialized || len == 0) return

            // Encrypt using GCTR
            gctr(plaintext, plaintextOffset, ciphertext, ciphertextOffset, len, counter, keySchedule)

            // Update authentication tag with ciphertext
            ghash.ghash(authTag, 0, ciphertext, ciphertextOffset, len)

            // Update counter
            incrementCounterBy(counter, 0, (len + 15) / 16)
            ctLen += len
        }

        fun decryptUpdate(
            ciphertext: ByteArray,
            ciphertextOffset: Int,
            plaintext: ByteArray,
            plaintextOffset: Int,
            len: Int
        ) {
            if (!initialized || len == 0) return

            // Update authentication tag with ciphertext before decryption
            ghash.ghash(authTag, 0, ciphertext, ciphertextOffset, len)

            // Decrypt using GCTR
            gctr(ciphertext, ciphertextOffset, plaintext, plaintextOffset, len, counter, keySchedule)

            // Update counter
            incrementCounterBy(counter, 0, (len + 15) / 16)
            ctLen += len
        }

        fun finish(tag: ByteArray, tagOffset: Int = 0) {
            if (!initialized) return

            // Append length block [len(A)]64 || [len(C)]64
            val lenBlock = ByteArray(16)
            val aadBits = aadLen * 8
            val ctBits = ctLen * 8

            // Store lengths in big-endian format
            for (i in 7 downTo 0) {
                lenBlock[7 - i] = (aadBits shr (i * 8)).toByte()
                lenBlock[15 - i] = (ctBits shr (i * 8)).toByte()
            }

            ghash.ghash(authTag, 0, lenBlock, 0, 16)

            // T = GCTR(J0, S)
            val finalTag = ByteArray(16)
            gctr(authTag, 0, finalTag, 0, 16, J0, keySchedule)

            // Copy requested tag length
            val tagLen = minOf(tag.size - tagOffset, 16)
            finalTag.copyInto(tag, tagOffset, 0, tagLen)
        }

        fun verify(tag: ByteArray, tagOffset: Int = 0): Boolean {
            val computedTag = ByteArray(16)
            finish(computedTag)

            // Constant-time comparison
            var diff = 0
            val tagLen = minOf(tag.size - tagOffset, 16)
            for (i in 0 until tagLen) {
                diff = diff or (tag[tagOffset + i].toInt() xor computedTag[i].toInt())
            }
            return diff == 0
        }
    }
}