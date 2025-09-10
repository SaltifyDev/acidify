package org.ntqqrev.acidify.crypto.aes

import kotlin.experimental.xor

/**
 * Core AES implementation with fundamental cryptographic operations.
 * Direct translation from aes_core.hpp
 */
object AesCore {

    // AES block size is always 128 bits (16 bytes)
    const val AES_BLOCK_SIZE: Int = 16

    // Supported key sizes
    enum class KeySize(val bytes: Int) {
        AES128(16),  // 128 bits
        AES192(24),  // 192 bits
        AES256(32)   // 256 bits
    }

    // Number of rounds based on key size
    fun getNumRounds(keySize: KeySize): Int {
        return when (keySize) {
            KeySize.AES128 -> 10
            KeySize.AES192 -> 12
            KeySize.AES256 -> 14
        }
    }

    // Maximum number of round keys
    const val MAX_ROUND_KEYS: Int = 15

    // Forward S-box for SubBytes operation
    private val sbox = byteArrayOf(
        0x63.toByte(),
        0x7c.toByte(),
        0x77.toByte(),
        0x7b.toByte(),
        0xf2.toByte(),
        0x6b.toByte(),
        0x6f.toByte(),
        0xc5.toByte(),
        0x30.toByte(),
        0x01.toByte(),
        0x67.toByte(),
        0x2b.toByte(),
        0xfe.toByte(),
        0xd7.toByte(),
        0xab.toByte(),
        0x76.toByte(),
        0xca.toByte(),
        0x82.toByte(),
        0xc9.toByte(),
        0x7d.toByte(),
        0xfa.toByte(),
        0x59.toByte(),
        0x47.toByte(),
        0xf0.toByte(),
        0xad.toByte(),
        0xd4.toByte(),
        0xa2.toByte(),
        0xaf.toByte(),
        0x9c.toByte(),
        0xa4.toByte(),
        0x72.toByte(),
        0xc0.toByte(),
        0xb7.toByte(),
        0xfd.toByte(),
        0x93.toByte(),
        0x26.toByte(),
        0x36.toByte(),
        0x3f.toByte(),
        0xf7.toByte(),
        0xcc.toByte(),
        0x34.toByte(),
        0xa5.toByte(),
        0xe5.toByte(),
        0xf1.toByte(),
        0x71.toByte(),
        0xd8.toByte(),
        0x31.toByte(),
        0x15.toByte(),
        0x04.toByte(),
        0xc7.toByte(),
        0x23.toByte(),
        0xc3.toByte(),
        0x18.toByte(),
        0x96.toByte(),
        0x05.toByte(),
        0x9a.toByte(),
        0x07.toByte(),
        0x12.toByte(),
        0x80.toByte(),
        0xe2.toByte(),
        0xeb.toByte(),
        0x27.toByte(),
        0xb2.toByte(),
        0x75.toByte(),
        0x09.toByte(),
        0x83.toByte(),
        0x2c.toByte(),
        0x1a.toByte(),
        0x1b.toByte(),
        0x6e.toByte(),
        0x5a.toByte(),
        0xa0.toByte(),
        0x52.toByte(),
        0x3b.toByte(),
        0xd6.toByte(),
        0xb3.toByte(),
        0x29.toByte(),
        0xe3.toByte(),
        0x2f.toByte(),
        0x84.toByte(),
        0x53.toByte(),
        0xd1.toByte(),
        0x00.toByte(),
        0xed.toByte(),
        0x20.toByte(),
        0xfc.toByte(),
        0xb1.toByte(),
        0x5b.toByte(),
        0x6a.toByte(),
        0xcb.toByte(),
        0xbe.toByte(),
        0x39.toByte(),
        0x4a.toByte(),
        0x4c.toByte(),
        0x58.toByte(),
        0xcf.toByte(),
        0xd0.toByte(),
        0xef.toByte(),
        0xaa.toByte(),
        0xfb.toByte(),
        0x43.toByte(),
        0x4d.toByte(),
        0x33.toByte(),
        0x85.toByte(),
        0x45.toByte(),
        0xf9.toByte(),
        0x02.toByte(),
        0x7f.toByte(),
        0x50.toByte(),
        0x3c.toByte(),
        0x9f.toByte(),
        0xa8.toByte(),
        0x51.toByte(),
        0xa3.toByte(),
        0x40.toByte(),
        0x8f.toByte(),
        0x92.toByte(),
        0x9d.toByte(),
        0x38.toByte(),
        0xf5.toByte(),
        0xbc.toByte(),
        0xb6.toByte(),
        0xda.toByte(),
        0x21.toByte(),
        0x10.toByte(),
        0xff.toByte(),
        0xf3.toByte(),
        0xd2.toByte(),
        0xcd.toByte(),
        0x0c.toByte(),
        0x13.toByte(),
        0xec.toByte(),
        0x5f.toByte(),
        0x97.toByte(),
        0x44.toByte(),
        0x17.toByte(),
        0xc4.toByte(),
        0xa7.toByte(),
        0x7e.toByte(),
        0x3d.toByte(),
        0x64.toByte(),
        0x5d.toByte(),
        0x19.toByte(),
        0x73.toByte(),
        0x60.toByte(),
        0x81.toByte(),
        0x4f.toByte(),
        0xdc.toByte(),
        0x22.toByte(),
        0x2a.toByte(),
        0x90.toByte(),
        0x88.toByte(),
        0x46.toByte(),
        0xee.toByte(),
        0xb8.toByte(),
        0x14.toByte(),
        0xde.toByte(),
        0x5e.toByte(),
        0x0b.toByte(),
        0xdb.toByte(),
        0xe0.toByte(),
        0x32.toByte(),
        0x3a.toByte(),
        0x0a.toByte(),
        0x49.toByte(),
        0x06.toByte(),
        0x24.toByte(),
        0x5c.toByte(),
        0xc2.toByte(),
        0xd3.toByte(),
        0xac.toByte(),
        0x62.toByte(),
        0x91.toByte(),
        0x95.toByte(),
        0xe4.toByte(),
        0x79.toByte(),
        0xe7.toByte(),
        0xc8.toByte(),
        0x37.toByte(),
        0x6d.toByte(),
        0x8d.toByte(),
        0xd5.toByte(),
        0x4e.toByte(),
        0xa9.toByte(),
        0x6c.toByte(),
        0x56.toByte(),
        0xf4.toByte(),
        0xea.toByte(),
        0x65.toByte(),
        0x7a.toByte(),
        0xae.toByte(),
        0x08.toByte(),
        0xba.toByte(),
        0x78.toByte(),
        0x25.toByte(),
        0x2e.toByte(),
        0x1c.toByte(),
        0xa6.toByte(),
        0xb4.toByte(),
        0xc6.toByte(),
        0xe8.toByte(),
        0xdd.toByte(),
        0x74.toByte(),
        0x1f.toByte(),
        0x4b.toByte(),
        0xbd.toByte(),
        0x8b.toByte(),
        0x8a.toByte(),
        0x70.toByte(),
        0x3e.toByte(),
        0xb5.toByte(),
        0x66.toByte(),
        0x48.toByte(),
        0x03.toByte(),
        0xf6.toByte(),
        0x0e.toByte(),
        0x61.toByte(),
        0x35.toByte(),
        0x57.toByte(),
        0xb9.toByte(),
        0x86.toByte(),
        0xc1.toByte(),
        0x1d.toByte(),
        0x9e.toByte(),
        0xe1.toByte(),
        0xf8.toByte(),
        0x98.toByte(),
        0x11.toByte(),
        0x69.toByte(),
        0xd9.toByte(),
        0x8e.toByte(),
        0x94.toByte(),
        0x9b.toByte(),
        0x1e.toByte(),
        0x87.toByte(),
        0xe9.toByte(),
        0xce.toByte(),
        0x55.toByte(),
        0x28.toByte(),
        0xdf.toByte(),
        0x8c.toByte(),
        0xa1.toByte(),
        0x89.toByte(),
        0x0d.toByte(),
        0xbf.toByte(),
        0xe6.toByte(),
        0x42.toByte(),
        0x68.toByte(),
        0x41.toByte(),
        0x99.toByte(),
        0x2d.toByte(),
        0x0f.toByte(),
        0xb0.toByte(),
        0x54.toByte(),
        0xbb.toByte(),
        0x16.toByte()
    )

    // Inverse S-box for InvSubBytes operation
    private val invSbox = byteArrayOf(
        0x52.toByte(),
        0x09.toByte(),
        0x6a.toByte(),
        0xd5.toByte(),
        0x30.toByte(),
        0x36.toByte(),
        0xa5.toByte(),
        0x38.toByte(),
        0xbf.toByte(),
        0x40.toByte(),
        0xa3.toByte(),
        0x9e.toByte(),
        0x81.toByte(),
        0xf3.toByte(),
        0xd7.toByte(),
        0xfb.toByte(),
        0x7c.toByte(),
        0xe3.toByte(),
        0x39.toByte(),
        0x82.toByte(),
        0x9b.toByte(),
        0x2f.toByte(),
        0xff.toByte(),
        0x87.toByte(),
        0x34.toByte(),
        0x8e.toByte(),
        0x43.toByte(),
        0x44.toByte(),
        0xc4.toByte(),
        0xde.toByte(),
        0xe9.toByte(),
        0xcb.toByte(),
        0x54.toByte(),
        0x7b.toByte(),
        0x94.toByte(),
        0x32.toByte(),
        0xa6.toByte(),
        0xc2.toByte(),
        0x23.toByte(),
        0x3d.toByte(),
        0xee.toByte(),
        0x4c.toByte(),
        0x95.toByte(),
        0x0b.toByte(),
        0x42.toByte(),
        0xfa.toByte(),
        0xc3.toByte(),
        0x4e.toByte(),
        0x08.toByte(),
        0x2e.toByte(),
        0xa1.toByte(),
        0x66.toByte(),
        0x28.toByte(),
        0xd9.toByte(),
        0x24.toByte(),
        0xb2.toByte(),
        0x76.toByte(),
        0x5b.toByte(),
        0xa2.toByte(),
        0x49.toByte(),
        0x6d.toByte(),
        0x8b.toByte(),
        0xd1.toByte(),
        0x25.toByte(),
        0x72.toByte(),
        0xf8.toByte(),
        0xf6.toByte(),
        0x64.toByte(),
        0x86.toByte(),
        0x68.toByte(),
        0x98.toByte(),
        0x16.toByte(),
        0xd4.toByte(),
        0xa4.toByte(),
        0x5c.toByte(),
        0xcc.toByte(),
        0x5d.toByte(),
        0x65.toByte(),
        0xb6.toByte(),
        0x92.toByte(),
        0x6c.toByte(),
        0x70.toByte(),
        0x48.toByte(),
        0x50.toByte(),
        0xfd.toByte(),
        0xed.toByte(),
        0xb9.toByte(),
        0xda.toByte(),
        0x5e.toByte(),
        0x15.toByte(),
        0x46.toByte(),
        0x57.toByte(),
        0xa7.toByte(),
        0x8d.toByte(),
        0x9d.toByte(),
        0x84.toByte(),
        0x90.toByte(),
        0xd8.toByte(),
        0xab.toByte(),
        0x00.toByte(),
        0x8c.toByte(),
        0xbc.toByte(),
        0xd3.toByte(),
        0x0a.toByte(),
        0xf7.toByte(),
        0xe4.toByte(),
        0x58.toByte(),
        0x05.toByte(),
        0xb8.toByte(),
        0xb3.toByte(),
        0x45.toByte(),
        0x06.toByte(),
        0xd0.toByte(),
        0x2c.toByte(),
        0x1e.toByte(),
        0x8f.toByte(),
        0xca.toByte(),
        0x3f.toByte(),
        0x0f.toByte(),
        0x02.toByte(),
        0xc1.toByte(),
        0xaf.toByte(),
        0xbd.toByte(),
        0x03.toByte(),
        0x01.toByte(),
        0x13.toByte(),
        0x8a.toByte(),
        0x6b.toByte(),
        0x3a.toByte(),
        0x91.toByte(),
        0x11.toByte(),
        0x41.toByte(),
        0x4f.toByte(),
        0x67.toByte(),
        0xdc.toByte(),
        0xea.toByte(),
        0x97.toByte(),
        0xf2.toByte(),
        0xcf.toByte(),
        0xce.toByte(),
        0xf0.toByte(),
        0xb4.toByte(),
        0xe6.toByte(),
        0x73.toByte(),
        0x96.toByte(),
        0xac.toByte(),
        0x74.toByte(),
        0x22.toByte(),
        0xe7.toByte(),
        0xad.toByte(),
        0x35.toByte(),
        0x85.toByte(),
        0xe2.toByte(),
        0xf9.toByte(),
        0x37.toByte(),
        0xe8.toByte(),
        0x1c.toByte(),
        0x75.toByte(),
        0xdf.toByte(),
        0x6e.toByte(),
        0x47.toByte(),
        0xf1.toByte(),
        0x1a.toByte(),
        0x71.toByte(),
        0x1d.toByte(),
        0x29.toByte(),
        0xc5.toByte(),
        0x89.toByte(),
        0x6f.toByte(),
        0xb7.toByte(),
        0x62.toByte(),
        0x0e.toByte(),
        0xaa.toByte(),
        0x18.toByte(),
        0xbe.toByte(),
        0x1b.toByte(),
        0xfc.toByte(),
        0x56.toByte(),
        0x3e.toByte(),
        0x4b.toByte(),
        0xc6.toByte(),
        0xd2.toByte(),
        0x79.toByte(),
        0x20.toByte(),
        0x9a.toByte(),
        0xdb.toByte(),
        0xc0.toByte(),
        0xfe.toByte(),
        0x78.toByte(),
        0xcd.toByte(),
        0x5a.toByte(),
        0xf4.toByte(),
        0x1f.toByte(),
        0xdd.toByte(),
        0xa8.toByte(),
        0x33.toByte(),
        0x88.toByte(),
        0x07.toByte(),
        0xc7.toByte(),
        0x31.toByte(),
        0xb1.toByte(),
        0x12.toByte(),
        0x10.toByte(),
        0x59.toByte(),
        0x27.toByte(),
        0x80.toByte(),
        0xec.toByte(),
        0x5f.toByte(),
        0x60.toByte(),
        0x51.toByte(),
        0x7f.toByte(),
        0xa9.toByte(),
        0x19.toByte(),
        0xb5.toByte(),
        0x4a.toByte(),
        0x0d.toByte(),
        0x2d.toByte(),
        0xe5.toByte(),
        0x7a.toByte(),
        0x9f.toByte(),
        0x93.toByte(),
        0xc9.toByte(),
        0x9c.toByte(),
        0xef.toByte(),
        0xa0.toByte(),
        0xe0.toByte(),
        0x3b.toByte(),
        0x4d.toByte(),
        0xae.toByte(),
        0x2a.toByte(),
        0xf5.toByte(),
        0xb0.toByte(),
        0xc8.toByte(),
        0xeb.toByte(),
        0xbb.toByte(),
        0x3c.toByte(),
        0x83.toByte(),
        0x53.toByte(),
        0x99.toByte(),
        0x61.toByte(),
        0x17.toByte(),
        0x2b.toByte(),
        0x04.toByte(),
        0x7e.toByte(),
        0xba.toByte(),
        0x77.toByte(),
        0xd6.toByte(),
        0x26.toByte(),
        0xe1.toByte(),
        0x69.toByte(),
        0x14.toByte(),
        0x63.toByte(),
        0x55.toByte(),
        0x21.toByte(),
        0x0c.toByte(),
        0x7d.toByte()
    )

    // Round constants for key expansion
    private val rcon = byteArrayOf(
        0x00.toByte(), 0x01.toByte(), 0x02.toByte(), 0x04.toByte(), 0x08.toByte(), 0x10.toByte(),
        0x20.toByte(), 0x40.toByte(), 0x80.toByte(), 0x1b.toByte(), 0x36.toByte()
    )

    // Galois field multiplication by 2
    private fun gfMul2(a: Byte): Byte {
        val aInt = a.toInt() and 0xFF
        return ((aInt shl 1) xor (if ((aInt and 0x80) != 0) 0x1b else 0)).toByte()
    }

    // Galois field multiplication by 3
    private fun gfMul3(a: Byte): Byte {
        return gfMul2(a) xor a
    }

    // Galois field multiplication
    fun gfMul(a: Byte, b: Byte): Byte {
        var result: Byte = 0
        var aVar = a
        var bVar = b.toInt() and 0xFF

        while (bVar != 0) {
            if ((bVar and 1) != 0) {
                result = result xor aVar
            }
            aVar = gfMul2(aVar)
            bVar = bVar shr 1
        }
        return result
    }

    // AES state type (4x4 byte matrix)
    // Using explicit type instead of typealias to avoid experimental feature

    // Create new state
    fun createState(): Array<ByteArray> = Array(4) { ByteArray(4) }

    // Convert block to state matrix (column-major order)
    fun blockToState(block: ByteArray, state: Array<ByteArray>) {
        for (c in 0 until 4) {
            for (r in 0 until 4) {
                state[r][c] = block[c * 4 + r]
            }
        }
    }

    // Convert state matrix to block (column-major order)
    fun stateToBlock(state: Array<ByteArray>, block: ByteArray) {
        for (c in 0 until 4) {
            for (r in 0 until 4) {
                block[c * 4 + r] = state[r][c]
            }
        }
    }

    // SubBytes transformation
    fun subBytes(state: Array<ByteArray>) {
        for (row in state) {
            for (i in row.indices) {
                row[i] = sbox[row[i].toInt() and 0xFF]
            }
        }
    }

    // Inverse SubBytes transformation
    fun invSubBytes(state: Array<ByteArray>) {
        for (row in state) {
            for (i in row.indices) {
                row[i] = invSbox[row[i].toInt() and 0xFF]
            }
        }
    }

    // ShiftRows transformation
    fun shiftRows(state: Array<ByteArray>) {
        // Row 0: no shift
        // Row 1: shift left by 1
        var temp = state[1][0]
        state[1][0] = state[1][1]
        state[1][1] = state[1][2]
        state[1][2] = state[1][3]
        state[1][3] = temp

        // Row 2: shift left by 2
        temp = state[2][0]
        val temp2 = state[2][1]
        state[2][0] = state[2][2]
        state[2][1] = state[2][3]
        state[2][2] = temp
        state[2][3] = temp2

        // Row 3: shift left by 3 (or right by 1)
        temp = state[3][3]
        state[3][3] = state[3][2]
        state[3][2] = state[3][1]
        state[3][1] = state[3][0]
        state[3][0] = temp
    }

    // Inverse ShiftRows transformation
    fun invShiftRows(state: Array<ByteArray>) {
        // Row 0: no shift
        // Row 1: shift right by 1
        var temp = state[1][3]
        state[1][3] = state[1][2]
        state[1][2] = state[1][1]
        state[1][1] = state[1][0]
        state[1][0] = temp

        // Row 2: shift right by 2
        temp = state[2][0]
        val temp2 = state[2][1]
        state[2][0] = state[2][2]
        state[2][1] = state[2][3]
        state[2][2] = temp
        state[2][3] = temp2

        // Row 3: shift right by 3 (or left by 1)
        temp = state[3][0]
        state[3][0] = state[3][1]
        state[3][1] = state[3][2]
        state[3][2] = state[3][3]
        state[3][3] = temp
    }

    // MixColumns transformation
    fun mixColumns(state: Array<ByteArray>) {
        for (c in 0 until 4) {
            val s0 = state[0][c]
            val s1 = state[1][c]
            val s2 = state[2][c]
            val s3 = state[3][c]

            state[0][c] = gfMul2(s0) xor gfMul3(s1) xor s2 xor s3
            state[1][c] = s0 xor gfMul2(s1) xor gfMul3(s2) xor s3
            state[2][c] = s0 xor s1 xor gfMul2(s2) xor gfMul3(s3)
            state[3][c] = gfMul3(s0) xor s1 xor s2 xor gfMul2(s3)
        }
    }

    // Inverse MixColumns transformation
    fun invMixColumns(state: Array<ByteArray>) {
        for (c in 0 until 4) {
            val s0 = state[0][c]
            val s1 = state[1][c]
            val s2 = state[2][c]
            val s3 = state[3][c]

            state[0][c] = gfMul(0x0e.toByte(), s0) xor gfMul(0x0b.toByte(), s1) xor gfMul(
                0x0d.toByte(),
                s2
            ) xor gfMul(0x09.toByte(), s3)
            state[1][c] = gfMul(0x09.toByte(), s0) xor gfMul(0x0e.toByte(), s1) xor gfMul(
                0x0b.toByte(),
                s2
            ) xor gfMul(0x0d.toByte(), s3)
            state[2][c] = gfMul(0x0d.toByte(), s0) xor gfMul(0x09.toByte(), s1) xor gfMul(
                0x0e.toByte(),
                s2
            ) xor gfMul(0x0b.toByte(), s3)
            state[3][c] = gfMul(0x0b.toByte(), s0) xor gfMul(0x0d.toByte(), s1) xor gfMul(
                0x09.toByte(),
                s2
            ) xor gfMul(0x0e.toByte(), s3)
        }
    }

    // AddRoundKey transformation
    fun addRoundKey(state: Array<ByteArray>, roundKey: ByteArray, offset: Int = 0) {
        for (c in 0 until 4) {
            for (r in 0 until 4) {
                state[r][c] = state[r][c] xor roundKey[offset + c * 4 + r]
            }
        }
    }

    // Key expansion for AES
    class KeySchedule {
        private val expandedKey = ByteArray(240) // Max size for AES-256
        var numRounds: Int = 0
            private set
        var keySize: KeySize = KeySize.AES128
            private set

        fun expandKey(key: ByteArray) {
            // Validate key size
            when (key.size) {
                16 -> keySize = KeySize.AES128
                24 -> keySize = KeySize.AES192
                32 -> keySize = KeySize.AES256
                else -> throw IllegalArgumentException("Invalid AES key size. Must be 16, 24, or 32 bytes.")
            }

            numRounds = getNumRounds(keySize)

            val keyWords = key.size / 4
            val totalWords = 4 * (numRounds + 1)

            // Copy the original key
            key.copyInto(expandedKey, 0, 0, key.size)

            // Expand the key - work with big-endian words
            for (i in keyWords until totalWords) {
                // Read word in big-endian order
                var temp = (expandedKey[(i - 1) * 4 + 0].toInt() and 0xFF shl 24) or
                        (expandedKey[(i - 1) * 4 + 1].toInt() and 0xFF shl 16) or
                        (expandedKey[(i - 1) * 4 + 2].toInt() and 0xFF shl 8) or
                        (expandedKey[(i - 1) * 4 + 3].toInt() and 0xFF)

                if (i % keyWords == 0) {
                    // RotWord: rotate left by 8 bits (move first byte to end)
                    temp = (temp shl 8) or (temp ushr 24)
                    // SubWord: apply S-box to each byte
                    temp = (sbox[temp and 0xff].toInt() and 0xFF) or
                            (sbox[(temp shr 8) and 0xff].toInt() and 0xFF shl 8) or
                            (sbox[(temp shr 16) and 0xff].toInt() and 0xFF shl 16) or
                            (sbox[(temp shr 24) and 0xff].toInt() and 0xFF shl 24)
                    // Apply round constant to the most significant byte (big-endian word)
                    temp = temp xor (rcon[i / keyWords].toInt() and 0xFF shl 24)
                } else if (keyWords > 6 && i % keyWords == 4) {
                    // Additional SubWord for AES-256
                    temp = (sbox[temp and 0xff].toInt() and 0xFF) or
                            (sbox[(temp shr 8) and 0xff].toInt() and 0xFF shl 8) or
                            (sbox[(temp shr 16) and 0xff].toInt() and 0xFF shl 16) or
                            (sbox[(temp shr 24) and 0xff].toInt() and 0xFF shl 24)
                }

                // Read previous word in big-endian order
                val prev = (expandedKey[(i - keyWords) * 4 + 0].toInt() and 0xFF shl 24) or
                        (expandedKey[(i - keyWords) * 4 + 1].toInt() and 0xFF shl 16) or
                        (expandedKey[(i - keyWords) * 4 + 2].toInt() and 0xFF shl 8) or
                        (expandedKey[(i - keyWords) * 4 + 3].toInt() and 0xFF)
                temp = temp xor prev

                // Store word in big-endian order
                expandedKey[i * 4 + 0] = (temp shr 24).toByte()
                expandedKey[i * 4 + 1] = (temp shr 16).toByte()
                expandedKey[i * 4 + 2] = (temp shr 8).toByte()
                expandedKey[i * 4 + 3] = temp.toByte()
            }
        }

        fun getRoundKey(round: Int): ByteArray {
            return expandedKey.sliceArray(round * 16 until (round + 1) * 16)
        }

        fun getRoundKeyAt(round: Int, destination: ByteArray, offset: Int = 0) {
            expandedKey.copyInto(destination, offset, round * 16, (round + 1) * 16)
        }
    }

    // Core AES encryption (single block)
    fun encryptBlock(
        input: ByteArray,
        inputOffset: Int,
        output: ByteArray,
        outputOffset: Int,
        keySchedule: KeySchedule
    ) {
        val state = createState()

        // Copy input to working array
        val inputBlock = ByteArray(16)
        input.copyInto(inputBlock, 0, inputOffset, inputOffset + 16)

        blockToState(inputBlock, state)

        // Initial round
        addRoundKey(state, keySchedule.getRoundKey(0))

        // Main rounds
        for (round in 1 until keySchedule.numRounds) {
            subBytes(state)
            shiftRows(state)
            mixColumns(state)
            addRoundKey(state, keySchedule.getRoundKey(round))
        }

        // Final round (no MixColumns)
        subBytes(state)
        shiftRows(state)
        addRoundKey(state, keySchedule.getRoundKey(keySchedule.numRounds))

        val outputBlock = ByteArray(16)
        stateToBlock(state, outputBlock)
        outputBlock.copyInto(output, outputOffset, 0, 16)
    }

    // Core AES decryption (single block)
    fun decryptBlock(
        input: ByteArray,
        inputOffset: Int,
        output: ByteArray,
        outputOffset: Int,
        keySchedule: KeySchedule
    ) {
        val state = createState()

        // Copy input to working array
        val inputBlock = ByteArray(16)
        input.copyInto(inputBlock, 0, inputOffset, inputOffset + 16)

        blockToState(inputBlock, state)

        // Initial round
        addRoundKey(state, keySchedule.getRoundKey(keySchedule.numRounds))

        // Main rounds (in reverse)
        for (round in keySchedule.numRounds - 1 downTo 1) {
            invShiftRows(state)
            invSubBytes(state)
            addRoundKey(state, keySchedule.getRoundKey(round))
            invMixColumns(state)
        }

        // Final round (no InvMixColumns)
        invShiftRows(state)
        invSubBytes(state)
        addRoundKey(state, keySchedule.getRoundKey(0))

        val outputBlock = ByteArray(16)
        stateToBlock(state, outputBlock)
        outputBlock.copyInto(output, outputOffset, 0, 16)
    }

    // Check for AES-NI support at runtime
    fun hasAesNi(): Boolean {
        // In Kotlin/JVM, we don't have direct access to CPU instructions
        // The JVM might use AES-NI internally through JCE providers
        return false
    }
}