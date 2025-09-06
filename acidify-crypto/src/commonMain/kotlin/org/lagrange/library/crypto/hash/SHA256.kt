package org.lagrange.library.crypto.hash

/**
 * SHA-256 hash algorithm implementation
 * Produces 256-bit (32 byte) hash values
 */
object SHA256 {
    const val BLOCK_SIZE = 64  // 512 bits
    const val DIGEST_SIZE = 32 // 256 bits

    // SHA256 constants (first 32 bits of fractional parts of cube roots of first 64 primes)
    private val K = intArrayOf(
        0x428a2f98, 0x71374491, 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
        0x3956c25b, 0x59f111f1, 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
        0xd807aa98.toInt(), 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
        0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
        0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e.toInt(), 0x92722c85.toInt(),
        0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
        0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814.toInt(), 0x8cc70208.toInt(),
        0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt()
    )

    // Initial hash values (first 32 bits of fractional parts of square roots of first 8 primes)
    private val H0 = intArrayOf(
        0x6a09e667, 0xbb67ae85.toInt(), 0x3c6ef372, 0xa54ff53a.toInt(),
        0x510e527f, 0x9b05688c.toInt(), 0x1f83d9ab, 0x5be0cd19
    )

    // Right rotate for 32-bit values
    private fun rotateRight(x: Int, n: Int): Int = (x ushr n) or (x shl (32 - n))

    // SHA256 specific functions
    private fun ch(x: Int, y: Int, z: Int): Int = (x and y) xor (x.inv() and z)
    private fun maj(x: Int, y: Int, z: Int): Int = (x and y) xor (x and z) xor (y and z)
    private fun sigma0(x: Int): Int = rotateRight(x, 2) xor rotateRight(x, 13) xor rotateRight(x, 22)
    private fun sigma1(x: Int): Int = rotateRight(x, 6) xor rotateRight(x, 11) xor rotateRight(x, 25)
    private fun gamma0(x: Int): Int = rotateRight(x, 7) xor rotateRight(x, 18) xor (x ushr 3)
    private fun gamma1(x: Int): Int = rotateRight(x, 17) xor rotateRight(x, 19) xor (x ushr 10)

    // Process a single 512-bit block
    private fun processBlock(block: ByteArray, offset: Int, state: IntArray) {
        val W = IntArray(64)

        // Prepare message schedule
        for (i in 0 until 16) {
            val idx = offset + i * 4
            W[i] = ((block[idx].toInt() and 0xFF) shl 24) or
                    ((block[idx + 1].toInt() and 0xFF) shl 16) or
                    ((block[idx + 2].toInt() and 0xFF) shl 8) or
                    (block[idx + 3].toInt() and 0xFF)
        }

        for (i in 16 until 64) {
            W[i] = gamma1(W[i - 2]) + W[i - 7] + gamma0(W[i - 15]) + W[i - 16]
        }

        // Working variables
        var a = state[0]
        var b = state[1]
        var c = state[2]
        var d = state[3]
        var e = state[4]
        var f = state[5]
        var g = state[6]
        var h = state[7]

        // Main loop
        for (i in 0 until 64) {
            val t1 = h + sigma1(e) + ch(e, f, g) + K[i] + W[i]
            val t2 = sigma0(a) + maj(a, b, c)

            h = g
            g = f
            f = e
            e = d + t1
            d = c
            c = b
            b = a
            a = t1 + t2
        }

        // Update state
        state[0] = state[0] + a
        state[1] = state[1] + b
        state[2] = state[2] + c
        state[3] = state[3] + d
        state[4] = state[4] + e
        state[5] = state[5] + f
        state[6] = state[6] + g
        state[7] = state[7] + h
    }

    /**
     * Compute SHA-256 hash of input data
     * @param data Input byte array
     * @return 32-byte SHA-256 digest
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

        // Add length in bits (big-endian for SHA256)
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
        for (i in 0 until 8) {
            val word = state[i]
            digest[i * 4] = ((word ushr 24) and 0xFF).toByte()
            digest[i * 4 + 1] = ((word ushr 16) and 0xFF).toByte()
            digest[i * 4 + 2] = ((word ushr 8) and 0xFF).toByte()
            digest[i * 4 + 3] = (word and 0xFF).toByte()
        }

        return digest
    }

    /**
     * Compute SHA-256 hash of string
     * @param text Input string
     * @return 32-byte SHA-256 digest
     */
    fun hash(text: String): ByteArray = hash(text.encodeToByteArray())

    /**
     * Convert digest to hexadecimal string
     * @param digest SHA-256 digest bytes
     * @return Hex string representation
     */
    fun toHex(digest: ByteArray): String =
        digest.joinToString("") {
            it.toInt().and(0xff).toString(16).padStart(2, '0')
        }

    /**
     * Compute SHA-256 hash and return as hex string
     * @param data Input byte array
     * @return SHA-256 hash as hex string
     */
    fun hashHex(data: ByteArray): String = toHex(hash(data))

    /**
     * Compute SHA-256 hash and return as hex string
     * @param text Input string
     * @return SHA-256 hash as hex string
     */
    fun hashHex(text: String): String = toHex(hash(text))
}

/**
 * HMAC-SHA256 implementation
 */
object HMACSHA256 {
    private const val BLOCK_SIZE = SHA256.BLOCK_SIZE
    private const val IPAD: Byte = 0x36
    private const val OPAD: Byte = 0x5c

    /**
     * Compute HMAC-SHA256
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-SHA256 digest
     */
    fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        // Prepare key
        val paddedKey = ByteArray(BLOCK_SIZE)

        if (key.size > BLOCK_SIZE) {
            // Hash the key if it's too long
            val hashedKey = SHA256.hash(key)
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
        val innerDigest = SHA256.hash(innerData)

        // Compute outer hash
        val outerData = keyOpad + innerDigest
        return SHA256.hash(outerData)
    }

    /**
     * Compute HMAC-SHA256 and return as hex string
     * @param key Secret key
     * @param data Data to authenticate
     * @return HMAC-SHA256 as hex string
     */
    fun hmacHex(key: ByteArray, data: ByteArray): String = SHA256.toHex(hmac(key, data))
}