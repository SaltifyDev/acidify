package org.lagrange.library.crypto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.lagrange.library.crypto.tea.TeaProvider
import kotlin.random.Random

class TEATest {
    @Test
    fun testCipherLength() {
        // Test cipher length calculation
        assertEquals(16, TeaProvider.getCipherLength(0))
        assertEquals(16, TeaProvider.getCipherLength(1))
        assertEquals(24, TeaProvider.getCipherLength(8))
        assertEquals(24, TeaProvider.getCipherLength(9))
        assertEquals(32, TeaProvider.getCipherLength(16))
        assertEquals(32, TeaProvider.getCipherLength(17))
        assertEquals(72, TeaProvider.getCipherLength(56))
        assertEquals(80, TeaProvider.getCipherLength(64))
    }
    
    @Test
    fun testPlainLength() {
        // Test plain length calculation
        // Note: getPlainLength uses: dataSize - ((dataSize and 7) + 3) - 7
        // For size 16: 16 - ((16 & 7) + 3) - 7 = 16 - (0 + 3) - 7 = 6
        // For size 24: 24 - ((24 & 7) + 3) - 7 = 24 - (0 + 3) - 7 = 14
        assertEquals(6, TeaProvider.getPlainLength(16))
        assertEquals(14, TeaProvider.getPlainLength(24))
        assertEquals(62, TeaProvider.getPlainLength(72))
        assertEquals(70, TeaProvider.getPlainLength(80))
    }
    
    @Test
    fun testBasicEncryptDecrypt() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = "Hello, World!".toByteArray()
        
        // Encrypt
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        
        // Verify cipher length
        val expectedLength = TeaProvider.getCipherLength(plaintext.size)
        assertEquals(expectedLength, ciphertext.size)
        
        // Decrypt
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        
        // Verify decrypted content
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testEmptyInput() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = ByteArray(0)
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        assertEquals(16, ciphertext.size) // Minimum cipher size
        
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testSingleByte() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = byteArrayOf(0x42)
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        assertEquals(16, ciphertext.size)
        
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testVariousSizes() {
        val key = ByteArray(16) { it.toByte() }
        val sizes = listOf(0, 1, 7, 8, 15, 16, 31, 32, 63, 64, 127, 128, 255, 256, 512, 1024)
        
        for (size in sizes) {
            val plaintext = ByteArray(size) { (it % 256).toByte() }
            
            val ciphertext = TeaProvider.encrypt(plaintext, key)
            val expectedLength = TeaProvider.getCipherLength(size)
            assertEquals(expectedLength, ciphertext.size, "Failed for size $size")
            
            val decrypted = TeaProvider.decrypt(ciphertext, key)
            assertArrayEquals(plaintext, decrypted, "Failed for size $size")
        }
    }
    
    @Test
    fun testRandomData() {
        val key = ByteArray(16) { Random.nextInt(256).toByte() }
        
        for (i in 0..10) {
            val size = Random.nextInt(0, 512)
            val plaintext = ByteArray(size) { Random.nextInt(256).toByte() }
            
            val ciphertext = TeaProvider.encrypt(plaintext, key)
            val decrypted = TeaProvider.decrypt(ciphertext, key)
            
            assertArrayEquals(plaintext, decrypted, "Failed for random data iteration $i, size $size")
        }
    }
    
    @Test
    fun testEncryptIntoBuffer() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = "Test buffer encryption".toByteArray()
        
        val destSize = TeaProvider.getCipherLength(plaintext.size)
        val dest = TeaProvider.encrypt(plaintext, key)
        assertEquals(destSize, dest.size)
        
        // Decrypt and verify
        val decrypted = TeaProvider.decrypt(dest, key)
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testDecryptIntoBuffer() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = "Test buffer decryption".toByteArray()
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        val dest = TeaProvider.decrypt(ciphertext, key)
        assertEquals(plaintext.size, dest.size)

        assertArrayEquals(plaintext, dest)
    }

    @Test
    fun testPaddingBehavior() {
        val key = ByteArray(16) { it.toByte() }
        
        // Test that padding is random (non-deterministic)
        val plaintext = "Padding test".toByteArray()
        val ciphertext1 = TeaProvider.encrypt(plaintext, key)
        val ciphertext2 = TeaProvider.encrypt(plaintext, key)
        
        // Due to random padding, ciphertexts should be different
        assertFalse(ciphertext1.contentEquals(ciphertext2))
        
        // But both should decrypt to the same plaintext
        assertArrayEquals(plaintext, TeaProvider.decrypt(ciphertext1, key))
        assertArrayEquals(plaintext, TeaProvider.decrypt(ciphertext2, key))
    }
    
    @Test
    fun testLargeData() {
        val key = ByteArray(16) { it.toByte() }
        val plaintext = ByteArray(10000) { (it % 256).toByte() }
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testAllZeroKey() {
        val key = ByteArray(16) { 0 }
        val plaintext = "Zero key test".toByteArray()
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testAllFFKey() {
        val key = ByteArray(16) { 0xFF.toByte() }
        val plaintext = "FF key test".toByteArray()
        
        val ciphertext = TeaProvider.encrypt(plaintext, key)
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        
        assertArrayEquals(plaintext, decrypted)
    }
    
    @Test
    fun testBinaryData() {
        val key = ByteArray(16) { it.toByte() }
        
        // Test with binary data including null bytes
        val binaryData = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0xFF.toByte(), 0xFE.toByte(), 0xFD.toByte(), 0x00)
        
        val ciphertext = TeaProvider.encrypt(binaryData, key)
        val decrypted = TeaProvider.decrypt(ciphertext, key)
        
        assertArrayEquals(binaryData, decrypted)
    }
}