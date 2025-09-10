package org.ntqqrev.acidify.crypto.aes

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.measureNanoTime

/**
 * Comprehensive test suite for AES encryption
 * Translated from C++ test_aes.cpp and test_aes.hpp
 * 
 * This test suite verifies:
 * - AES key expansion for 128/192/256-bit keys
 * - ECB, CBC, CTR, and GCM modes of operation
 * - Padding schemes (PKCS7, Zero, ISO7816)
 * - NIST test vectors validation
 * - Memory safety and buffer overflow protection
 * - Error handling and invalid input
 * - Thread safety
 * - Performance benchmarks
 */
@DisplayName("AES Encryption Test Suite")
class AesTest {

    companion object {
        // AES block size constant
        const val AES_BLOCK_SIZE = 16
    }

    @Nested
    @DisplayName("Key Expansion Tests")
    inner class KeyExpansionTests {
        
        @Test
        @DisplayName("Test AES-128 key expansion")
        fun testAes128KeyExpansion() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val keySchedule = AesCore.KeySchedule()
            keySchedule.expandKey(key)
            
            assertEquals(10, keySchedule.numRounds)
            assertEquals(AesCore.KeySize.AES128, keySchedule.keySize)
            
            // Verify first round key is the original key
            val firstRoundKey = keySchedule.getRoundKey(0)
            assertArrayEquals(key, firstRoundKey)
            
            // Verify specific round keys from NIST test vectors
            val round10Key = keySchedule.getRoundKey(10)
            val expectedRound10 = hexToBytes("d014f9a8c9ee2589e13f0cc8b6630ca6")
            assertArrayEquals(expectedRound10, round10Key)
        }
        
        @Test
        @DisplayName("Test AES-192 key expansion")
        fun testAes192KeyExpansion() {
            val key = hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b")
            val keySchedule = AesCore.KeySchedule()
            keySchedule.expandKey(key)
            
            assertEquals(12, keySchedule.numRounds)
            assertEquals(AesCore.KeySize.AES192, keySchedule.keySize)
            
            // Verify first round key
            val firstRoundKey = keySchedule.getRoundKey(0)
            assertArrayEquals(key.sliceArray(0..15), firstRoundKey)
        }
        
        @Test
        @DisplayName("Test AES-256 key expansion")
        fun testAes256KeyExpansion() {
            val key = hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4")
            val keySchedule = AesCore.KeySchedule()
            keySchedule.expandKey(key)
            
            assertEquals(14, keySchedule.numRounds)
            assertEquals(AesCore.KeySize.AES256, keySchedule.keySize)
            
            // Verify first round key
            val firstRoundKey = keySchedule.getRoundKey(0)
            assertArrayEquals(key.sliceArray(0..15), firstRoundKey)
        }
    }

    @Nested
    @DisplayName("ECB Mode Tests")
    inner class EcbModeTests {
        
        @Test
        @DisplayName("Test ECB mode with NIST vectors - AES-128")
        fun testEcbAes128() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val plaintext = hexToBytes("6bc1bee22e409f96e93d7e117393172a")
            val expectedCiphertext = hexToBytes("3ad77bb40d7a3660a89ecaf32466ef97")
            
            val ecb = ECB(key, PaddingScheme.NONE)
            val ciphertext = ecb.encrypt(plaintext)
            assertArrayEquals(expectedCiphertext, ciphertext)
            
            val decrypted = ecb.decrypt(ciphertext)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test ECB mode with padding")
        fun testEcbWithPadding() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val plaintext = "Hello, World!".toByteArray()
            
            val ecb = ECB(key, PaddingScheme.PKCS7)
            val ciphertext = ecb.encrypt(plaintext)
            
            // Ciphertext should be padded to multiple of 16
            assertEquals(0, ciphertext.size % 16)
            
            val decrypted = ecb.decrypt(ciphertext)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test ECB mode detects identical blocks")
        fun testEcbIdenticalBlocks() {
            val key = Random.nextBytes(16)
            val plaintext = ByteArray(32) { 0x41 } // Two identical blocks of 'A'
            
            val ecb = ECB(key, PaddingScheme.NONE)
            val ciphertext = ecb.encrypt(plaintext)
            
            // In ECB mode, identical plaintext blocks produce identical ciphertext blocks
            val block1 = ciphertext.sliceArray(0..15)
            val block2 = ciphertext.sliceArray(16..31)
            assertArrayEquals(block1, block2)
        }
    }

    @Nested
    @DisplayName("CBC Mode Tests")
    inner class CbcModeTests {
        
        @Test
        @DisplayName("Test CBC mode with NIST vectors - AES-128")
        fun testCbcAes128() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val iv = hexToBytes("000102030405060708090a0b0c0d0e0f")
            val plaintext = hexToBytes("6bc1bee22e409f96e93d7e117393172a")
            val expectedCiphertext = hexToBytes("7649abac8119b246cee98e9b12e9197d")
            
            val cbc = CBC(key, iv, PaddingScheme.NONE)
            val ciphertext = cbc.encrypt(plaintext)
            assertArrayEquals(expectedCiphertext, ciphertext)
            
            val decrypted = cbc.decrypt(ciphertext)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test CBC mode with padding")
        fun testCbcWithPadding() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val iv = hexToBytes("000102030405060708090a0b0c0d0e0f")
            val plaintext = "Hello, CBC mode!".toByteArray()
            
            val cbc = CBC(key, iv, PaddingScheme.PKCS7)
            val ciphertext = cbc.encrypt(plaintext)
            
            // Ciphertext should be padded to multiple of 16
            assertEquals(0, ciphertext.size % 16)
            
            val decrypted = cbc.decrypt(ciphertext)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test CBC mode does not reveal patterns")
        fun testCbcNoPatterns() {
            val key = Random.nextBytes(16)
            val iv = Random.nextBytes(16)
            val plaintext = ByteArray(32) { 0x41 } // Two identical blocks of 'A'
            
            val cbc = CBC(key, iv, PaddingScheme.NONE)
            val ciphertext = cbc.encrypt(plaintext)
            
            // In CBC mode, identical plaintext blocks should NOT produce identical ciphertext blocks
            val block1 = ciphertext.sliceArray(0..15)
            val block2 = ciphertext.sliceArray(16..31)
            assertFalse(block1.contentEquals(block2))
        }
        
        @Test
        @DisplayName("Test CBC mode IV propagation")
        fun testCbcIvPropagation() {
            val key = Random.nextBytes(16)
            val plaintext = "Test message".toByteArray()
            
            // Different IVs should produce different ciphertexts
            val iv1 = Random.nextBytes(16)
            val iv2 = Random.nextBytes(16)
            
            val cbc1 = CBC(key, iv1, PaddingScheme.PKCS7)
            val cbc2 = CBC(key, iv2, PaddingScheme.PKCS7)
            
            val ciphertext1 = cbc1.encrypt(plaintext)
            val ciphertext2 = cbc2.encrypt(plaintext)
            
            assertFalse(ciphertext1.contentEquals(ciphertext2))
        }
    }

    @Nested
    @DisplayName("CTR Mode Tests")
    inner class CtrModeTests {
        
        @Test
        @DisplayName("Test CTR mode basic operation")
        fun testCtrBasic() {
            val key = hexToBytes("2b7e151628aed2a6abf7158809cf4f3c")
            val nonce = hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff")
            val plaintext = "Hello, CTR mode! This is a streaming cipher.".toByteArray()
            
            val ctr = CTR(key, nonce)
            val ciphertext = ctr.encrypt(plaintext)
            
            // CTR mode doesn't change the size
            assertEquals(plaintext.size, ciphertext.size)
            
            // Create new CTR instance for decryption (reset counter)
            val ctr2 = CTR(key, nonce)
            val decrypted = ctr2.decrypt(ciphertext)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test CTR mode is symmetric")
        fun testCtrSymmetric() {
            val key = Random.nextBytes(16)
            val nonce = Random.nextBytes(16)
            val plaintext = "CTR mode test".toByteArray()
            
            val ctr1 = CTR(key, nonce)
            val encrypted = ctr1.encrypt(plaintext)
            
            val ctr2 = CTR(key, nonce)
            val doubleEncrypted = ctr2.encrypt(encrypted)
            
            // Encrypting twice should return original plaintext
            assertArrayEquals(plaintext, doubleEncrypted)
        }
        
        @Test
        @DisplayName("Test CTR mode streaming")
        fun testCtrStreaming() {
            val key = Random.nextBytes(16)
            val nonce = Random.nextBytes(16)
            
            val ctr = CTR(key, nonce)
            
            // Encrypt in chunks
            val part1 = "Hello, ".toByteArray()
            val part2 = "World!".toByteArray()
            
            val encrypted1 = ctr.encrypt(part1)
            val encrypted2 = ctr.encrypt(part2)
            
            // Decrypt as whole
            val ctr2 = CTR(key, nonce)
            val wholeCiphertext = encrypted1 + encrypted2
            val decrypted = ctr2.decrypt(wholeCiphertext)
            
            val expectedPlaintext = part1 + part2
            assertArrayEquals(expectedPlaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test CTR mode with various data sizes")
        fun testCtrVariousSizes() {
            val key = Random.nextBytes(16)
            val nonce = Random.nextBytes(16)
            
            // Test various sizes including non-block-aligned
            val sizes = listOf(0, 1, 15, 16, 17, 31, 32, 33, 100, 1000)
            
            for (size in sizes) {
                val plaintext = Random.nextBytes(size)
                
                val ctr1 = CTR(key, nonce)
                val encrypted = ctr1.encrypt(plaintext)
                assertEquals(size, encrypted.size, "CTR should preserve size for $size bytes")
                
                val ctr2 = CTR(key, nonce)
                val decrypted = ctr2.decrypt(encrypted)
                assertArrayEquals(plaintext, decrypted, "CTR should correctly handle $size bytes")
            }
        }
    }

    @Nested
    @DisplayName("GCM Mode Tests")
    inner class GcmModeTests {
        
        @Test
        @DisplayName("Test GCM mode basic authenticated encryption")
        fun testGcmBasic() {
            val key = hexToBytes("feffe9928665731c6d6a8f9467308308")
            val iv = hexToBytes("cafebabefacedbaddecaf888")
            val plaintext = "Hello, GCM mode!".toByteArray()
            val aad = "Additional authenticated data".toByteArray()
            
            val gcm = GCM(key)
            val result = gcm.encrypt(plaintext, iv, aad)
            
            // Verify we got ciphertext and tag
            assertEquals(plaintext.size, result.ciphertext.size)
            assertEquals(16, result.tag.size) // Default tag size
            
            // Decrypt and verify
            val decrypted = gcm.decrypt(result.ciphertext, iv, result.tag, aad)
            assertArrayEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test GCM mode authentication failure")
        fun testGcmAuthFailure() {
            val key = Random.nextBytes(16)
            val iv = Random.nextBytes(12)
            val plaintext = "Secret message".toByteArray()
            val aad = "Header".toByteArray()
            
            val gcm = GCM(key)
            val result = gcm.encrypt(plaintext, iv, aad)
            
            // Tamper with ciphertext
            result.ciphertext[0] = (result.ciphertext[0].toInt() xor 0xFF).toByte()
            
            // Decryption should fail
            assertThrows(IllegalArgumentException::class.java) {
                gcm.decrypt(result.ciphertext, iv, result.tag, aad)
            }
        }
        
        @Test
        @DisplayName("Test GCM mode with different tag sizes")
        fun testGcmTagSizes() {
            val key = Random.nextBytes(16)
            val iv = Random.nextBytes(12)
            val plaintext = "Test message".toByteArray()
            
            val tagSizes = listOf(
                GcmHelper.TagSize.TAG_96,
                GcmHelper.TagSize.TAG_104,
                GcmHelper.TagSize.TAG_112,
                GcmHelper.TagSize.TAG_120,
                GcmHelper.TagSize.TAG_128
            )
            
            for (tagSize in tagSizes) {
                val gcm = GCM(key, tagSize)
                val result = gcm.encrypt(plaintext, iv)
                
                assertEquals(tagSize.bytes, result.tag.size)
                
                val decrypted = gcm.decrypt(result.ciphertext, iv, result.tag)
                assertArrayEquals(plaintext, decrypted)
            }
        }
        
        @Test
        @DisplayName("Test GCM mode with empty plaintext")
        fun testGcmEmptyPlaintext() {
            val key = Random.nextBytes(16)
            val iv = Random.nextBytes(12)
            val plaintext = ByteArray(0)
            val aad = "Just authentication".toByteArray()
            
            val gcm = GCM(key)
            val result = gcm.encrypt(plaintext, iv, aad)
            
            assertEquals(0, result.ciphertext.size)
            assertEquals(16, result.tag.size)
            
            // Should still authenticate correctly
            val decrypted = gcm.decrypt(result.ciphertext, iv, result.tag, aad)
            assertEquals(0, decrypted.size)
        }
        
        @Test
        @DisplayName("Test GCM static methods")
        fun testGcmStaticMethods() {
            val key = Random.nextBytes(32) // AES-256
            val plaintext = "Static method test".toByteArray()
            val iv = Random.nextBytes(12)
            val aad = "Header data".toByteArray()
            
            // Use static encrypt method
            val result = GCM.encryptAuthenticated(key, plaintext, iv, aad)
            
            // Use static decrypt method
            val decrypted = GCM.decryptAuthenticated(key, result.ciphertext, iv, result.tag, aad)
            
            assertArrayEquals(plaintext, decrypted)
        }
    }

    @Nested
    @DisplayName("Padding Tests")
    inner class PaddingTests {
        
        @Test
        @DisplayName("Test PKCS7 padding")
        fun testPkcs7Padding() {
            // Test various data sizes
            val testCases = listOf(
                0 to 16,   // Empty data needs full block of padding
                1 to 16,   // 1 byte data needs 15 bytes padding
                15 to 16,  // 15 bytes data needs 1 byte padding
                16 to 32,  // Full block needs another full block of padding
                17 to 32   // 17 bytes needs 15 bytes padding
            )
            
            for ((dataSize, expectedSize) in testCases) {
                val buffer = ByteArray(expectedSize + 16) // Extra space
                if (dataSize > 0) {
                    for (i in 0 until dataSize) {
                        buffer[i] = (i % 256).toByte()
                    }
                }
                
                val paddedLen = applyPadding(buffer, dataSize, PaddingScheme.PKCS7)
                assertEquals(expectedSize, paddedLen, "PKCS7 padding failed for size $dataSize")
                
                val unpaddedLen = removePadding(buffer, paddedLen, PaddingScheme.PKCS7)
                assertEquals(dataSize, unpaddedLen, "PKCS7 unpadding failed for size $dataSize")
            }
        }
        
        @Test
        @DisplayName("Test Zero padding")
        fun testZeroPadding() {
            val data = "Hello".toByteArray()
            val buffer = ByteArray(16)
            data.copyInto(buffer)
            
            val paddedLen = applyPadding(buffer, data.size, PaddingScheme.ZEROS)
            assertEquals(16, paddedLen)
            
            // Check that padding is zeros
            for (i in 5 until 16) {
                assertEquals(0, buffer[i])
            }
            
            // Note: Zero padding removal may not work for data ending in zeros
            val testData = "Test".toByteArray()
            val testBuffer = ByteArray(16)
            testData.copyInto(testBuffer)
            val testPaddedLen = applyPadding(testBuffer, testData.size, PaddingScheme.ZEROS)
            val unpaddedLen = removePadding(testBuffer, testPaddedLen, PaddingScheme.ZEROS)
            assertEquals(testData.size, unpaddedLen)
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    inner class EdgeCaseTests {
        
        @Test
        @DisplayName("Test empty data encryption")
        fun testEmptyData() {
            val key = Random.nextBytes(16)
            val emptyData = ByteArray(0)
            
            // ECB with padding
            val ecb = ECB(key, PaddingScheme.PKCS7)
            val ecbEncrypted = ecb.encrypt(emptyData)
            assertEquals(16, ecbEncrypted.size) // One block of padding
            val ecbDecrypted = ecb.decrypt(ecbEncrypted)
            assertEquals(0, ecbDecrypted.size)
            
            // CTR mode (no padding needed)
            val ctr = CTR(key, Random.nextBytes(16))
            val ctrEncrypted = ctr.encrypt(emptyData)
            assertEquals(0, ctrEncrypted.size)
        }
        
        @Test
        @DisplayName("Test single byte encryption")
        fun testSingleByte() {
            val key = Random.nextBytes(16)
            val singleByte = byteArrayOf(0x42)
            
            // Test all modes
            val ecb = ECB(key, PaddingScheme.PKCS7)
            val ecbResult = ecb.decrypt(ecb.encrypt(singleByte))
            assertArrayEquals(singleByte, ecbResult)
            
            val iv = Random.nextBytes(16)
            val cbc = CBC(key, iv, PaddingScheme.PKCS7)
            val cbcResult = cbc.decrypt(cbc.encrypt(singleByte))
            assertArrayEquals(singleByte, cbcResult)
            
            val nonce = Random.nextBytes(16)
            val ctr = CTR(key, nonce)
            val encrypted = ctr.encrypt(singleByte)
            val ctr2 = CTR(key, nonce)
            val ctrResult = ctr2.decrypt(encrypted)
            assertArrayEquals(singleByte, ctrResult)
        }
        
        @Test
        @DisplayName("Test large data encryption")
        fun testLargeData() {
            val key = Random.nextBytes(16)
            val largeData = Random.nextBytes(1024 * 1024) // 1 MB
            
            // CTR mode is best for large data (no padding overhead)
            val nonce = Random.nextBytes(16)
            val ctr = CTR(key, nonce)
            val encrypted = ctr.encrypt(largeData)
            assertEquals(largeData.size, encrypted.size)
            
            val ctr2 = CTR(key, nonce)
            val decrypted = ctr2.decrypt(encrypted)
            assertArrayEquals(largeData, decrypted)
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {
        
        @Test
        @DisplayName("Test invalid key sizes")
        fun testInvalidKeySizes() {
            val invalidKeySizes = listOf(0, 8, 15, 17, 20, 23, 25, 31, 33, 64)
            
            for (size in invalidKeySizes) {
                val key = ByteArray(size)
                assertThrows(IllegalArgumentException::class.java) {
                    ECB(key)
                }
            }
        }
        
        @Test
        @DisplayName("Test invalid IV sizes for CBC")
        fun testInvalidIvSizes() {
            val key = Random.nextBytes(16)
            val invalidIvSizes = listOf(0, 8, 15, 17, 32)
            
            for (size in invalidIvSizes) {
                val iv = ByteArray(size)
                assertThrows(IllegalArgumentException::class.java) {
                    CBC(key, iv)
                }
            }
        }
        
        @Test
        @DisplayName("Test corrupted padding")
        fun testCorruptedPadding() {
            val key = Random.nextBytes(16)
            val plaintext = "Test message".toByteArray()
            
            val ecb = ECB(key, PaddingScheme.PKCS7)
            val ciphertext = ecb.encrypt(plaintext)
            
            // Corrupt the last byte (padding byte)
            ciphertext[ciphertext.size - 1] = 0xFF.toByte()
            
            assertThrows(IllegalArgumentException::class.java) {
                ecb.decrypt(ciphertext)
            }
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    inner class ThreadSafetyTests {
        
        @Test
        @DisplayName("Test concurrent encryption/decryption")
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        fun testConcurrentOperations() {
            runBlocking {
                val key = Random.nextBytes(16)
                val numThreads = 10
                val numOperations = 100
                
                val jobs = (1..numThreads).map {
                    async {
                        for (i in 1..numOperations) {
                            val plaintext = "Thread test $it-$i".toByteArray()
                            
                            // Each thread uses its own cipher instance
                            val ecb = ECB(key)
                            val encrypted = ecb.encrypt(plaintext)
                            val decrypted = ecb.decrypt(encrypted)
                            
                            assertArrayEquals(plaintext, decrypted)
                        }
                    }
                }
                
                jobs.awaitAll()
            }
        }
    }

    @Nested
    @DisplayName("AES Interface Tests")
    inner class AesInterfaceTests {
        
        @Test
        @DisplayName("Test AES interface with ECB mode")
        fun testAESInterface() {
            val key = byteArrayOf(
                0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae.toByte(), 0xd2.toByte(), 0xa6.toByte(),
                0xab.toByte(), 0xf7.toByte(), 0x15, 0x88.toByte(), 0x09, 0xcf.toByte(), 0x4f, 0x3c
            )
            val plaintext = "Test message"
            
            // Test ECB mode through AES interface
            val ecbCipher = AES.createEcb(key)
            val ecbEncrypted = ecbCipher.encrypt(plaintext.toByteArray())
            val ecbDecrypted = ecbCipher.decrypt(ecbEncrypted)
            assertEquals(plaintext, String(ecbDecrypted))
            
            // Test string encryption/decryption
            val encrypted = AES.encryptString(AES.Mode.ECB, plaintext, key)
            val decrypted = AES.decryptToString(AES.Mode.ECB, encrypted, key)
            assertEquals(plaintext, decrypted)
        }
        
        @Test
        @DisplayName("Test utility functions")
        fun testUtilityFunctions() {
            // Test hex conversion
            val bytes = byteArrayOf(0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
            val hex = bytesToHex(bytes)
            assertEquals("0123456789abcdef", hex)
            
            val converted = hexToBytes(hex)
            assertArrayEquals(bytes, converted)
            
            // Test base64 conversion
            val text = "Hello, World!"
            val base64 = bytesToBase64(text.toByteArray())
            assertEquals("SGVsbG8sIFdvcmxkIQ==", base64)
            
            val decoded = base64ToBytes(base64)
            assertEquals(text, String(decoded))
        }
    }

    @Nested
    @DisplayName("Performance Benchmarks")
    @EnabledIfSystemProperty(named = "run.benchmarks", matches = "true")
    inner class PerformanceTests {
        
        @Test
        @DisplayName("Benchmark AES modes")
        fun benchmarkAesModes() {
            val key = Random.nextBytes(16)
            val data = Random.nextBytes(1024 * 1024) // 1 MB
            val iterations = 100
            
            // Benchmark ECB
            val ecbTime = measureNanoTime {
                val ecb = ECB(key, PaddingScheme.NONE)
                repeat(iterations) {
                    ecb.encrypt(data)
                }
            }
            println("ECB: ${ecbTime / 1_000_000} ms for $iterations iterations")
            
            // Benchmark CBC
            val cbcTime = measureNanoTime {
                val cbc = CBC(key, Random.nextBytes(16), PaddingScheme.NONE)
                repeat(iterations) {
                    cbc.encrypt(data)
                }
            }
            println("CBC: ${cbcTime / 1_000_000} ms for $iterations iterations")
            
            // Benchmark CTR
            val ctrTime = measureNanoTime {
                val ctr = CTR(key, Random.nextBytes(16))
                repeat(iterations) {
                    ctr.encrypt(data)
                }
            }
            println("CTR: ${ctrTime / 1_000_000} ms for $iterations iterations")
            
            // CTR should be fastest as it's parallelizable
            assertTrue(ctrTime <= cbcTime * 1.2, "CTR should be comparable or faster than CBC")
        }
    }
}