package org.ntqqrev.acidify.crypto.hash

/**
 * MD5 hash algorithm implementation
 * Produces 128-bit (16 byte) hash values
 */
object MD5 {
    const val BLOCK_SIZE = 64  // 512 bits
    const val DIGEST_SIZE = 16 // 128 bits

    // MD5 constants - precomputed from sin function
    private val K = intArrayOf(
        // Round 1
        0xd76aa478.toInt(), 0xe8c7b756.toInt(), 0x242070db, 0xc1bdceee.toInt(),
        0xf57c0faf.toInt(), 0x4787c62a, 0xa8304613.toInt(), 0xfd469501.toInt(),
        0x698098d8, 0x8b44f7af.toInt(), 0xffff5bb1.toInt(), 0x895cd7be.toInt(),
        0x6b901122, 0xfd987193.toInt(), 0xa679438e.toInt(), 0x49b40821,
        // Round 2
        0xf61e2562.toInt(), 0xc040b340.toInt(), 0x265e5a51, 0xe9b6c7aa.toInt(),
        0xd62f105d.toInt(), 0x02441453, 0xd8a1e681.toInt(), 0xe7d3fbc8.toInt(),
        0x21e1cde6, 0xc33707d6.toInt(), 0xf4d50d87.toInt(), 0x455a14ed,
        0xa9e3e905.toInt(), 0xfcefa3f8.toInt(), 0x676f02d9, 0x8d2a4c8a.toInt(),
        // Round 3
        0xfffa3942.toInt(), 0x8771f681.toInt(), 0x6d9d6122, 0xfde5380c.toInt(),
        0xa4beea44.toInt(), 0x4bdecfa9, 0xf6bb4b60.toInt(), 0xbebfbc70.toInt(),
        0x289b7ec6, 0xeaa127fa.toInt(), 0xd4ef3085.toInt(), 0x04881d05,
        0xd9d4d039.toInt(), 0xe6db99e5.toInt(), 0x1fa27cf8, 0xc4ac5665.toInt(),
        // Round 4
        0xf4292244.toInt(), 0x432aff97, 0xab9423a7.toInt(), 0xfc93a039.toInt(),
        0x655b59c3, 0x8f0ccc92.toInt(), 0xffeff47d.toInt(), 0x85845dd1.toInt(),
        0x6fa87e4f, 0xfe2ce6e0.toInt(), 0xa3014314.toInt(), 0x4e0811a1,
        0xf7537e82.toInt(), 0xbd3af235.toInt(), 0x2ad7d2bb, 0xeb86d391.toInt()
    )

    // Shift amounts for each round
    private val S = intArrayOf(
        // Round 1
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        // Round 2
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        // Round 3
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        // Round 4
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    )

    // Initial hash values
    private val H0 = intArrayOf(0x67452301, 0xefcdab89.toInt(), 0x98badcfe.toInt(), 0x10325476)

    // MD5 auxiliary functions
    private fun f(x: Int, y: Int, z: Int): Int = (x and y) or (x.inv() and z)
    private fun g(x: Int, y: Int, z: Int): Int = (x and z) or (y and z.inv())
    private fun h(x: Int, y: Int, z: Int): Int = x xor y xor z
    private fun i(x: Int, y: Int, z: Int): Int = y xor (x or z.inv())

    // Left rotate function
    private fun rotateLeft(x: Int, n: Int): Int = (x shl n) or (x ushr (32 - n))

    // Process a single 512-bit block
    private fun processBlock(block: ByteArray, offset: Int, state: IntArray) {
        val M = IntArray(16)

        // Load block into 32-bit words (little-endian)
        for (i in 0 until 16) {
            val idx = offset + i * 4
            M[i] = (block[idx].toInt() and 0xFF) or
                    ((block[idx + 1].toInt() and 0xFF) shl 8) or
                    ((block[idx + 2].toInt() and 0xFF) shl 16) or
                    ((block[idx + 3].toInt() and 0xFF) shl 24)
        }

        // Working variables
        var a = state[0]
        var b = state[1]
        var c = state[2]
        var d = state[3]

        // Main loop - 64 operations
        for (i in 0 until 64) {
            val (f, g) = when {
                i < 16 -> {
                    // Round 1
                    f(b, c, d) to i
                }

                i < 32 -> {
                    // Round 2
                    g(b, c, d) to ((5 * i + 1) % 16)
                }

                i < 48 -> {
                    // Round 3
                    h(b, c, d) to ((3 * i + 5) % 16)
                }

                else -> {
                    // Round 4
                    i(b, c, d) to ((7 * i) % 16)
                }
            }

            val temp = f + a + K[i] + M[g]
            a = d
            d = c
            c = b
            b = b + rotateLeft(temp, S[i])
        }

        // Update state
        state[0] = state[0] + a
        state[1] = state[1] + b
        state[2] = state[2] + c
        state[3] = state[3] + d
    }

    /**
     * Compute MD5 hash of input data
     * @param data Input byte array
     * @return 16-byte MD5 digest
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

        // Add length in bits (little-endian)
        val bitLength = totalLength * 8
        val lengthOffset = paddingBlock.size - 8
        for (i in 0 until 8) {
            paddingBlock[lengthOffset + i] = ((bitLength ushr (i * 8)) and 0xFF).toByte()
        }

        // Process padding block(s)
        for (i in 0 until paddingBlock.size step BLOCK_SIZE) {
            processBlock(paddingBlock, i, state)
        }

        // Convert state to bytes (little-endian)
        val digest = ByteArray(DIGEST_SIZE)
        for (i in 0 until 4) {
            val word = state[i]
            digest[i * 4] = (word and 0xFF).toByte()
            digest[i * 4 + 1] = ((word ushr 8) and 0xFF).toByte()
            digest[i * 4 + 2] = ((word ushr 16) and 0xFF).toByte()
            digest[i * 4 + 3] = ((word ushr 24) and 0xFF).toByte()
        }

        return digest
    }

    /**
     * Compute MD5 hash of string
     * @param text Input string
     * @return 16-byte MD5 digest
     */
    fun hash(text: String): ByteArray = hash(text.encodeToByteArray())

    /**
     * Convert digest to hexadecimal string
     * @param digest MD5 digest bytes
     * @return Hex string representation
     */
    fun toHex(digest: ByteArray): String =
        digest.joinToString("") {
            it.toInt().and(0xff).toString(16).padStart(2, '0')
        }

    /**
     * Compute MD5 hash and return as hex string
     * @param data Input byte array
     * @return MD5 hash as hex string
     */
    fun hashHex(data: ByteArray): String = toHex(hash(data))

    /**
     * Compute MD5 hash and return as hex string
     * @param text Input string
     * @return MD5 hash as hex string
     */
    fun hashHex(text: String): String = toHex(hash(text))
}

/**
 * HMAC-MD5 implementation
 */
object HMACMD5 {
    private const val BLOCK_SIZE = MD5.BLOCK_SIZE
    private const val IPAD: Byte = 0x36
    private const val OPAD: Byte = 0x5c

    /**
     * Compute HMAC-MD5
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-MD5 digest
     */
    fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        // Prepare key
        val paddedKey = ByteArray(BLOCK_SIZE)

        if (key.size > BLOCK_SIZE) {
            // Hash the key if it's too long
            val hashedKey = MD5.hash(key)
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
        val innerDigest = MD5.hash(innerData)

        // Compute outer hash
        val outerData = keyOpad + innerDigest
        return MD5.hash(outerData)
    }

    /**
     * Compute HMAC-MD5 and return as hex string
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-MD5 as hex string
     */
    fun hmacHex(key: ByteArray, data: ByteArray): String = MD5.toHex(hmac(key, data))
}