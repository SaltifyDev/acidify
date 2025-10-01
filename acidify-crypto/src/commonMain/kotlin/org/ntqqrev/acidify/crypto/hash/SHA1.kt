package org.ntqqrev.acidify.crypto.hash

/**
 * SHA-1 hash algorithm implementation
 * Produces 160-bit (20 byte) hash values
 */
object SHA1 {
    const val BLOCK_SIZE = 64  // 512 bits
    const val DIGEST_SIZE = 20 // 160 bits

    // Initial hash values
    private val H0 = intArrayOf(
        0x67452301,
        0xefcdab89.toInt(),
        0x98badcfe.toInt(),
        0x10325476,
        0xc3d2e1f0.toInt()
    )

    // Left rotate for 32-bit values
    private fun rotateLeft(x: Int, n: Int): Int = (x shl n) or (x ushr (32 - n))

    // SHA1 auxiliary functions
    private fun f(t: Int, b: Int, c: Int, d: Int): Int = when {
        t < 20 -> (b and c) or (b.inv() and d)
        t < 40 -> b xor c xor d
        t < 60 -> (b and c) or (b and d) or (c and d)
        else -> b xor c xor d
    }

    // SHA1 constants
    private fun k(t: Int): Int = when {
        t < 20 -> 0x5a827999
        t < 40 -> 0x6ed9eba1
        t < 60 -> 0x8f1bbcdc.toInt()
        else -> 0xca62c1d6.toInt()
    }

    // Process a single 512-bit block
    private fun processBlock(block: ByteArray, offset: Int, state: IntArray) {
        val W = IntArray(80)

        // Prepare message schedule (first 16 words from input)
        for (i in 0 until 16) {
            val idx = offset + i * 4
            W[i] = ((block[idx].toInt() and 0xFF) shl 24) or
                    ((block[idx + 1].toInt() and 0xFF) shl 16) or
                    ((block[idx + 2].toInt() and 0xFF) shl 8) or
                    (block[idx + 3].toInt() and 0xFF)
        }

        // Extend the 16 words into 80 words
        for (i in 16 until 80) {
            W[i] = rotateLeft(W[i - 3] xor W[i - 8] xor W[i - 14] xor W[i - 16], 1)
        }

        // Working variables
        var a = state[0]
        var b = state[1]
        var c = state[2]
        var d = state[3]
        var e = state[4]

        // Main loop (80 rounds)
        for (t in 0 until 80) {
            val temp = rotateLeft(a, 5) + f(t, b, c, d) + e + W[t] + k(t)
            e = d
            d = c
            c = rotateLeft(b, 30)
            b = a
            a = temp
        }

        // Update state
        state[0] = state[0] + a
        state[1] = state[1] + b
        state[2] = state[2] + c
        state[3] = state[3] + d
        state[4] = state[4] + e
    }

    /**
     * Compute SHA-1 hash of input data
     * @param data Input byte array
     * @return 20-byte SHA-1 digest
     */
    fun hash(data: ByteArray): ByteArray {
        val state = H0.copyOf()
        val totalLength = data.size.toLong()

        // Process complete blocks
        var processedBytes = 0
        while (processedBytes + BLOCK_SIZE <= data.size) {
            processBlock(data, processedBytes, state)
            processedBytes += BLOCK_SIZE
        }

        // Handle remaining bytes and padding
        val remaining = data.size - processedBytes
        val paddingBlock = ByteArray(if (remaining < BLOCK_SIZE - 8) BLOCK_SIZE else BLOCK_SIZE * 2)

        // Copy remaining data
        data.copyInto(paddingBlock, 0, processedBytes, processedBytes + remaining)

        // Add padding
        paddingBlock[remaining] = 0x80.toByte()

        // Add length in bits (big-endian for SHA1)
        val bitLength = totalLength * 8
        val lengthOffset = paddingBlock.size - 8
        for (i in 0 until 8) {
            paddingBlock[lengthOffset + i] = ((bitLength ushr ((7 - i) * 8)) and 0xFF).toByte()
        }

        // Process padding block(s)
        for (i in 0 until paddingBlock.size step BLOCK_SIZE) {
            processBlock(paddingBlock, i, state)
        }

        // Convert state to bytes (big-endian)
        val digest = ByteArray(DIGEST_SIZE)
        for (i in 0 until 5) {
            val word = state[i]
            digest[i * 4] = ((word ushr 24) and 0xFF).toByte()
            digest[i * 4 + 1] = ((word ushr 16) and 0xFF).toByte()
            digest[i * 4 + 2] = ((word ushr 8) and 0xFF).toByte()
            digest[i * 4 + 3] = (word and 0xFF).toByte()
        }

        return digest
    }

    /**
     * Compute SHA-1 hash of string
     * @param text Input string
     * @return 20-byte SHA-1 digest
     */
    fun hash(text: String): ByteArray = hash(text.encodeToByteArray())

    /**
     * Convert digest to hexadecimal string
     * @param digest SHA-1 digest bytes
     * @return Hex string representation
     */
    fun toHex(digest: ByteArray): String =
        digest.joinToString("") {
            it.toInt().and(0xff).toString(16).padStart(2, '0')
        }

    /**
     * Compute SHA-1 hash and return as hex string
     * @param data Input byte array
     * @return SHA-1 hash as hex string
     */
    fun hashHex(data: ByteArray): String = toHex(hash(data))

    /**
     * Compute SHA-1 hash and return as hex string
     * @param text Input string
     * @return SHA-1 hash as hex string
     */
    fun hashHex(text: String): String = toHex(hash(text))
}

/**
 * HMAC-SHA1 implementation
 */
object HMACSHA1 {
    private const val BLOCK_SIZE = SHA1.BLOCK_SIZE
    private const val IPAD: Byte = 0x36
    private const val OPAD: Byte = 0x5c

    /**
     * Compute HMAC-SHA1
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-SHA1 digest
     */
    fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        // Prepare key
        val paddedKey = ByteArray(BLOCK_SIZE)

        if (key.size > BLOCK_SIZE) {
            // Hash the key if it's too long
            val hashedKey = SHA1.hash(key)
            hashedKey.copyInto(paddedKey, 0, 0, hashedKey.size)
        } else {
            // Use the key as-is
            key.copyInto(paddedKey, 0, 0, key.size)
        }

        // Create inner and outer padded keys
        val keyIpad = ByteArray(BLOCK_SIZE)
        val keyOpad = ByteArray(BLOCK_SIZE)

        for (i in 0 until BLOCK_SIZE) {
            keyIpad[i] = (paddedKey[i].toInt() xor IPAD.toInt()).toByte()
            keyOpad[i] = (paddedKey[i].toInt() xor OPAD.toInt()).toByte()
        }

        // Compute inner hash
        val innerData = keyIpad + data
        val innerDigest = SHA1.hash(innerData)

        // Compute outer hash
        val outerData = keyOpad + innerDigest
        return SHA1.hash(outerData)
    }

    /**
     * Compute HMAC-SHA1 and return as hex string
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-SHA1 as hex string
     */
    fun hmacHex(key: ByteArray, data: ByteArray): String = SHA1.toHex(hmac(key, data))
}

