package org.ntqqrev.acidify.crypto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.ntqqrev.acidify.crypto.hash.HMACSHA256
import org.ntqqrev.acidify.crypto.hash.SHA256

class SHA256Test {
    
    // NIST test vectors
    @ParameterizedTest
    @CsvSource(
        "'', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855'",
        "'abc', 'ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad'",
        "'abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq', '248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1'",
        "'abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu', 'cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1'"
    )
    fun testNISTVectors(input: String, expectedHex: String) {
        val digest = SHA256.hash(input.toByteArray())
        assertEquals(expectedHex, SHA256.toHex(digest))
    }
    
    @Test
    fun testMillionA() {
        // Test vector: one million 'a' characters
        val millionA = ByteArray(1_000_000) { 'a'.code.toByte() }
        assertEquals("cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0", SHA256.hashHex(millionA))
    }
    
    @Test
    fun testEmptyInput() {
        val empty = ByteArray(0)
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", SHA256.hashHex(empty))
    }
    
    @Test
    fun testSingleByte() {
        val singleA = byteArrayOf('a'.code.toByte())
        assertEquals("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb", SHA256.hashHex(singleA))
    }
    
    @Test
    fun testBlockBoundaries() {
        // Test exactly 64 bytes (one block)
        val input64 = ByteArray(64) { 'A'.code.toByte() }
        val digest64 = SHA256.hash(input64)
        assertEquals(32, digest64.size)
        
        // Test 55 bytes (near padding boundary)
        val input55 = ByteArray(55) { 'B'.code.toByte() }
        val digest55 = SHA256.hash(input55)
        assertEquals(32, digest55.size)
        
        // Test 56 bytes (at padding boundary)
        val input56 = ByteArray(56) { 'C'.code.toByte() }
        val digest56 = SHA256.hash(input56)
        assertEquals(32, digest56.size)
        
        // Test 65 bytes (one block + 1 byte)
        val input65 = ByteArray(65) { 'D'.code.toByte() }
        val digest65 = SHA256.hash(input65)
        assertEquals(32, digest65.size)
    }
    
    @Test
    fun testCommonPhrases() {
        assertEquals(
            "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592",
            SHA256.hashHex("The quick brown fox jumps over the lazy dog")
        )
        
        assertEquals(
            "ef537f25c895bfa782526529a9b63d97aa631564d5d789c2b765448c8635fb6c",
            SHA256.hashHex("The quick brown fox jumps over the lazy dog.")
        )
        
        assertEquals(
            "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f",
            SHA256.hashHex("Hello, World!")
        )
    }
    
    @Test
    fun testStringOverloads() {
        val testString = "Test String"
        val fromString = SHA256.hash(testString)
        val fromBytes = SHA256.hash(testString.toByteArray())
        assertArrayEquals(fromString, fromBytes)
        
        val hexFromString = SHA256.hashHex(testString)
        val hexFromBytes = SHA256.hashHex(testString.toByteArray())
        assertEquals(hexFromString, hexFromBytes)
    }
    
    // HMAC-SHA256 Tests
    @Test
    fun testHMACRFC4231() {
        // Test Case 1
        val key1 = ByteArray(20) { 0x0b }
        val data1 = "Hi There".toByteArray()
        assertEquals(
            "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
            HMACSHA256.hmacHex(key1, data1)
        )
        
        // Test Case 2
        val key2 = "Jefe".toByteArray()
        val data2 = "what do ya want for nothing?".toByteArray()
        assertEquals(
            "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843",
            HMACSHA256.hmacHex(key2, data2)
        )
        
        // Test Case 3
        val key3 = ByteArray(20) { 0xaa.toByte() }
        val data3 = ByteArray(50) { 0xdd.toByte() }
        assertEquals(
            "773ea91e36800e46854db8ebd09181a72959098b3ef8c122d9635514ced565fe",
            HMACSHA256.hmacHex(key3, data3)
        )
    }
    
    @Test
    fun testHMACSimple() {
        val key = "key".toByteArray()
        val data = "The quick brown fox jumps over the lazy dog".toByteArray()
        assertEquals(
            "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",
            HMACSHA256.hmacHex(key, data)
        )
    }
    
    @Test
    fun testHMACLongKey() {
        // Test with key longer than block size (64 bytes)
        val longKey = ByteArray(100) { 'K'.code.toByte() }
        val data = "Test data".toByteArray()
        val digest = HMACSHA256.hmac(longKey, data)
        assertEquals(32, digest.size)
    }
    
    @Test
    fun testHMACEmptyData() {
        val key = "key".toByteArray()
        val emptyData = ByteArray(0)
        val digest = HMACSHA256.hmac(key, emptyData)
        assertEquals(32, digest.size)
    }
    
    @Test
    fun testHMACEmptyKey() {
        val emptyKey = ByteArray(0)
        val data = "data".toByteArray()
        val digest = HMACSHA256.hmac(emptyKey, data)
        assertEquals(32, digest.size)
    }
    
    @Test
    fun testBinaryData() {
        // Test single byte 0x00
        val singleZero = byteArrayOf(0x00)
        val digest1 = SHA256.hash(singleZero)
        assertEquals(32, digest1.size)
        
        // Test single byte 0xFF
        val singleFF = byteArrayOf(0xFF.toByte())
        val digest2 = SHA256.hash(singleFF)
        assertEquals(32, digest2.size)
        
        // Test sequence
        val sequence = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        val digest3 = SHA256.hash(sequence)
        assertEquals(32, digest3.size)
    }
    
    @Test
    fun testConsistency() {
        val input = "Consistency test"
        val hash1 = SHA256.hashHex(input)
        val hash2 = SHA256.hashHex(input)
        assertEquals(hash1, hash2)
    }
    
    @Test
    fun testDifferentInputs() {
        val input1 = "Input 1"
        val input2 = "Input 2"
        val hash1 = SHA256.hashHex(input1)
        val hash2 = SHA256.hashHex(input2)
        assertNotEquals(hash1, hash2)
    }
    
    @Test
    fun testLargeInput() {
        // Test with 10MB input
        val largeInput = ByteArray(10 * 1024 * 1024) { 'X'.code.toByte() }
        val digest = SHA256.hash(largeInput)
        assertEquals(32, digest.size)
        
        // Verify it produces consistent results
        val digest2 = SHA256.hash(largeInput)
        assertArrayEquals(digest, digest2)
    }
    
    @Test
    fun testHexConversion() {
        val input = "Hello"
        val digest = SHA256.hash(input.toByteArray())
        val hex = SHA256.toHex(digest)
        
        // Verify hex string properties
        assertEquals(64, hex.length) // 32 bytes * 2 hex chars per byte
        
        // Verify all characters are valid hex
        for (c in hex) {
            assertTrue((c in '0'..'9') || (c in 'a'..'f'))
        }
        
        // Known value check
        assertEquals("dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f", SHA256.hashHex("Hello, World!"))
    }
}