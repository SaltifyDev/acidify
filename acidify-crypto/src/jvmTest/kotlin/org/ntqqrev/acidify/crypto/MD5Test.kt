package org.ntqqrev.acidify.crypto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.ntqqrev.acidify.crypto.hash.HMACMD5
import org.ntqqrev.acidify.crypto.hash.MD5
import java.util.stream.Stream

class MD5Test {
    
    // RFC 1321 test vectors
    @ParameterizedTest
    @CsvSource(
        "'', 'd41d8cd98f00b204e9800998ecf8427e'",
        "'a', '0cc175b9c0f1b6a831c399e269772661'",
        "'abc', '900150983cd24fb0d6963f7d28e17f72'",
        "'message digest', 'f96b697d7cb7938d525a2f31aaf161d0'",
        "'abcdefghijklmnopqrstuvwxyz', 'c3fcd3d76192e4007dfb496cca67e13b'",
        "'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789', 'd174ab98d277d9f5a5611c2c9f419d9f'",
        "'12345678901234567890123456789012345678901234567890123456789012345678901234567890', '57edf4a22be3c955ac49da2e2107b67a'"
    )
    fun testRFC1321Vectors(input: String, expectedHex: String) {
        val digest = MD5.hash(input.toByteArray())
        assertEquals(expectedHex, MD5.toHex(digest))
    }
    
    // Additional test vectors
    @ParameterizedTest
    @CsvSource(
        "'The quick brown fox jumps over the lazy dog', '9e107d9d372bb6826bd81d3542a419d6'",
        "'The quick brown fox jumps over the lazy dog.', 'e4d909c290d0fb1ca068ffaddf22cbd0'",
        "'Hello, World!', '65a8e27d8879283831b664bd8b7f0ad4'",
        "'Wikipedia', '9c677286866aad38f8e9b660f5411814'",
        "'MD5 has been utilized in a wide variety of cryptographic applications', '6689184a55dd9365bc0bb938e450d467'"
    )
    fun testAdditionalVectors(input: String, expectedHex: String) {
        val digest = MD5.hash(input.toByteArray())
        assertEquals(expectedHex, MD5.toHex(digest))
    }
    
    @Test
    fun testBinaryData() {
        // Test single byte 0x00
        val singleZero = byteArrayOf(0x00)
        assertEquals("93b885adfe0da089cdf634904fd59f71", MD5.hashHex(singleZero))
        
        // Test single byte 0xFF
        val singleFF = byteArrayOf(0xFF.toByte())
        assertEquals("00594fd4f42ba43fc1ca0427a0576295", MD5.hashHex(singleFF))
        
        // Test sequence 0x00-0x07
        val sequence = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        assertEquals("3677509751ccf61539174d2b9635a7bf", MD5.hashHex(sequence))
        
        // Test 55 bytes of 0x00
        val zeros55 = ByteArray(55) { 0x00 }
        assertEquals("c9ea3314b91c9fd4e38f9432064fd1f2", MD5.hashHex(zeros55))
        
        // Test 56 bytes of 0x00
        val zeros56 = ByteArray(56) { 0x00 }
        assertEquals("e3c4dd21a9171fd39d208efa09bf7883", MD5.hashHex(zeros56))
        
        // Test 64 bytes of 0x00
        val zeros64 = ByteArray(64) { 0x00 }
        assertEquals("3b5d3c7d207e37dceeedd301e35e2e58", MD5.hashHex(zeros64))
    }
    
    @Test
    fun testLargeInputs() {
        // Test exactly 64 bytes (one block)
        val input64 = ByteArray(64) { 'A'.code.toByte() }
        assertEquals("d289a97565bc2d27ac8b8545a5ddba45", MD5.hashHex(input64))
        
        // Test near padding boundary
        val input55 = ByteArray(55) { 'C'.code.toByte() }
        val digest55 = MD5.hash(input55)
        assertEquals(16, digest55.size)
        
        // Test at padding boundary
        val input56 = ByteArray(56) { 'D'.code.toByte() }
        val digest56 = MD5.hash(input56)
        assertEquals(16, digest56.size)
    }
    
    @Test
    fun testMillionA() {
        // Test vector: one million 'a' characters
        val millionA = ByteArray(1_000_000) { 'a'.code.toByte() }
        assertEquals("7707d6ae4e027c70eea2a935c2296f21", MD5.hashHex(millionA))
    }
    
    @Test
    fun testEmptyInput() {
        val empty = ByteArray(0)
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5.hashHex(empty))
    }
    
    @Test
    fun testStringOverloads() {
        val testString = "Hello, World!"
        val fromString = MD5.hash(testString)
        val fromBytes = MD5.hash(testString.toByteArray())
        assertArrayEquals(fromString, fromBytes)
        
        val hexFromString = MD5.hashHex(testString)
        val hexFromBytes = MD5.hashHex(testString.toByteArray())
        assertEquals(hexFromString, hexFromBytes)
        assertEquals("65a8e27d8879283831b664bd8b7f0ad4", hexFromString)
    }
    
    // HMAC-MD5 Tests
    @Test
    fun testHMACRFC2202() {
        // Test Case 1
        val key1 = ByteArray(16) { 0x0b }
        val data1 = "Hi There".toByteArray()
        assertEquals("9294727a3638bb1c13f48ef8158bfc9d", HMACMD5.hmacHex(key1, data1))
        
        // Test Case 2
        val key2 = "Jefe".toByteArray()
        val data2 = "what do ya want for nothing?".toByteArray()
        assertEquals("750c783e6ab0b503eaa86e310a5db738", HMACMD5.hmacHex(key2, data2))
        
        // Test Case 3
        val key3 = ByteArray(16) { 0xaa.toByte() }
        val data3 = ByteArray(50) { 0xdd.toByte() }
        assertEquals("56be34521d144c88dbb8c733f0e8b3f6", HMACMD5.hmacHex(key3, data3))
    }
    
    @Test
    fun testHMACSimple() {
        val key = "key".toByteArray()
        val data = "The quick brown fox jumps over the lazy dog".toByteArray()
        assertEquals("80070713463e7749b90c2dc24911e275", HMACMD5.hmacHex(key, data))
    }
    
    @Test
    fun testHMACLongKey() {
        // Test with key longer than block size (64 bytes)
        val longKey = ByteArray(100) { 'K'.code.toByte() }
        val data = "Test data".toByteArray()
        val digest = HMACMD5.hmac(longKey, data)
        assertEquals(16, digest.size)
    }
    
    @Test
    fun testHMACEmptyData() {
        val key = "key".toByteArray()
        val emptyData = ByteArray(0)
        val digest = HMACMD5.hmac(key, emptyData)
        assertEquals(16, digest.size)
    }
    
    @Test
    fun testHMACEmptyKey() {
        val emptyKey = ByteArray(0)
        val data = "data".toByteArray()
        val digest = HMACMD5.hmac(emptyKey, data)
        assertEquals(16, digest.size)
    }
    
    @Test
    fun testConsistencyAcrossCalls() {
        val input = "Test consistency"
        val hash1 = MD5.hashHex(input)
        val hash2 = MD5.hashHex(input)
        assertEquals(hash1, hash2)
    }
    
    @Test
    fun testDifferentInputsProduceDifferentHashes() {
        val input1 = "Input 1"
        val input2 = "Input 2"
        val hash1 = MD5.hashHex(input1)
        val hash2 = MD5.hashHex(input2)
        assertNotEquals(hash1, hash2)
    }
    
    companion object {
        @JvmStatic
        fun binaryTestData(): Stream<BinaryTestCase> = Stream.of(
            BinaryTestCase(byteArrayOf(0x00), "93b885adfe0da089cdf634904fd59f71"),
            BinaryTestCase(byteArrayOf(0xFF.toByte()), "00594fd4f42ba43fc1ca0427a0576295"),
            BinaryTestCase(ByteArray(55) { 0x00 }, "c9ea3314b91c9fd4e38f9432064fd1f2"),
            BinaryTestCase(ByteArray(56) { 0x00 }, "e3c4dd21a9171fd39d208efa09bf7883"),
            BinaryTestCase(ByteArray(64) { 0x00 }, "3b5d3c7d207e37dceeedd301e35e2e58")
        )
    }
    
    data class BinaryTestCase(val input: ByteArray, val expectedHex: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BinaryTestCase
            if (!input.contentEquals(other.input)) return false
            if (expectedHex != other.expectedHex) return false
            return true
        }

        override fun hashCode(): Int {
            var result = input.contentHashCode()
            result = 31 * result + expectedHex.hashCode()
            return result
        }
    }
    
    @ParameterizedTest
    @MethodSource("binaryTestData")
    fun testBinaryDataParameterized(testCase: BinaryTestCase) {
        assertEquals(testCase.expectedHex, MD5.hashHex(testCase.input))
    }
}