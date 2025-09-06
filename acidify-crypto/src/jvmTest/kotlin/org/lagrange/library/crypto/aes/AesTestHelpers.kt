package org.lagrange.library.crypto.aes

import java.nio.ByteBuffer
import java.security.SecureRandom
import kotlin.experimental.and

/**
 * Test helper functions for AES encryption tests
 * Translated from C++ test_aes.hpp helpers namespace
 */
object AesTestHelpers {
    
    /**
     * Convert hex string to bytes
     * @param hex The hex string to convert
     * @return ByteArray representation of the hex string
     * @throws IllegalArgumentException if hex string has odd length
     */
    fun hexToBytes(hex: String): ByteArray {
        if (hex.length % 2 != 0) {
            throw IllegalArgumentException("Hex string must have even length")
        }
        
        return ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
    
    /**
     * Convert bytes to hex string
     * @param bytes The byte array to convert
     * @return Hex string representation
     */
    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
    
    /**
     * Compare two byte arrays
     * @param a First byte array
     * @param b Second byte array
     * @return true if arrays are equal, false otherwise
     */
    fun compareBytes(a: ByteArray, b: ByteArray): Boolean {
        return a.contentEquals(b)
    }
    
    /**
     * Generate random bytes
     * @param length Number of bytes to generate
     * @return ByteArray of random bytes
     */
    fun generateRandomBytes(length: Int): ByteArray {
        val random = SecureRandom()
        return ByteArray(length).also { random.nextBytes(it) }
    }
    
    /**
     * Print test vector info
     * @param name Name of the test vector
     * @param data Data to print
     * @param maxBytes Maximum number of bytes to print
     */
    fun printTestVector(name: String, data: ByteArray, maxBytes: Int = 32) {
        print("$name: ")
        val printLen = minOf(data.size, maxBytes)
        for (i in 0 until printLen) {
            print("%02x".format(data[i]))
        }
        if (data.size > maxBytes) {
            print("... (${data.size} bytes total)")
        }
        println()
    }
}

/**
 * NIST test vectors for AES encryption
 * Translated from C++ test_aes.hpp nist_vectors namespace
 */
object NistVectors {
    
    /**
     * Test vector data class
     */
    data class TestVector(
        val description: String,
        val key: ByteArray,
        val plaintext: ByteArray,
        val ciphertext: ByteArray,
        val iv: ByteArray = ByteArray(0),
        val aad: ByteArray = ByteArray(0),
        val tag: ByteArray = ByteArray(0)
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is TestVector) return false
            
            return description == other.description &&
                   key.contentEquals(other.key) &&
                   plaintext.contentEquals(other.plaintext) &&
                   ciphertext.contentEquals(other.ciphertext) &&
                   iv.contentEquals(other.iv) &&
                   aad.contentEquals(other.aad) &&
                   tag.contentEquals(other.tag)
        }
        
        override fun hashCode(): Int {
            var result = description.hashCode()
            result = 31 * result + key.contentHashCode()
            result = 31 * result + plaintext.contentHashCode()
            result = 31 * result + ciphertext.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            result = 31 * result + aad.contentHashCode()
            result = 31 * result + tag.contentHashCode()
            return result
        }
    }
    
    /**
     * NIST AES-128 ECB test vectors
     */
    fun getAes128EcbVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-128 ECB Test Vector 1",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("3ad77bb40d7a3660a89ecaf32466ef97")
            ),
            TestVector(
                "AES-128 ECB Test Vector 2",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("f5d3d58503b9699de785895a96fdbaaf")
            ),
            TestVector(
                "AES-128 ECB Test Vector 3",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("30c81c46a35ce411e5fbc1191a0a52ef"),
                AesTestHelpers.hexToBytes("43b1cd7f598ece23881b00e3ed030688")
            ),
            TestVector(
                "AES-128 ECB Test Vector 4",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("f69f2445df4f9b17ad2b417be66c3710"),
                AesTestHelpers.hexToBytes("7b0c785e27e8ad3f8223207104725dd4")
            )
        )
    }
    
    /**
     * NIST AES-192 ECB test vectors
     */
    fun getAes192EcbVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-192 ECB Test Vector 1",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("bd334f1d6e45f25ff712a214571fa5cc")
            ),
            TestVector(
                "AES-192 ECB Test Vector 2",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("974104846d0ad3ad7734ecb3ecee4eef")
            ),
            TestVector(
                "AES-192 ECB Test Vector 3",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("30c81c46a35ce411e5fbc1191a0a52ef"),
                AesTestHelpers.hexToBytes("ef7afd2270e2e60adce0ba2face6444e")
            ),
            TestVector(
                "AES-192 ECB Test Vector 4",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("f69f2445df4f9b17ad2b417be66c3710"),
                AesTestHelpers.hexToBytes("9a4b41ba738d6c72fb16691603c18e0e")
            )
        )
    }
    
    /**
     * NIST AES-256 ECB test vectors
     */
    fun getAes256EcbVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-256 ECB Test Vector 1",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("f3eed1bdb5d2a03c064b5a7e3db181f8")
            ),
            TestVector(
                "AES-256 ECB Test Vector 2",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("591ccb10d410ed26dc5ba74a31362870")
            ),
            TestVector(
                "AES-256 ECB Test Vector 3",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("30c81c46a35ce411e5fbc1191a0a52ef"),
                AesTestHelpers.hexToBytes("b6ed21b99ca6f4f9f153e7b1beafed1d")
            ),
            TestVector(
                "AES-256 ECB Test Vector 4",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("f69f2445df4f9b17ad2b417be66c3710"),
                AesTestHelpers.hexToBytes("23304b7a39f9f3ff067d8d8f9e24ecc7")
            )
        )
    }
    
    /**
     * NIST AES-128 CBC test vectors
     */
    fun getAes128CbcVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-128 CBC Test Vector 1",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("7649abac8119b246cee98e9b12e9197d"),
                AesTestHelpers.hexToBytes("000102030405060708090a0b0c0d0e0f")
            ),
            TestVector(
                "AES-128 CBC Test Vector 2",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("5086cb9b507219ee95db113a917678b2"),
                AesTestHelpers.hexToBytes("7649abac8119b246cee98e9b12e9197d")
            ),
            TestVector(
                "AES-128 CBC Test Vector 3",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("30c81c46a35ce411e5fbc1191a0a52ef"),
                AesTestHelpers.hexToBytes("73bed6b8e3c1743b7116e69e22229516"),
                AesTestHelpers.hexToBytes("5086cb9b507219ee95db113a917678b2")
            ),
            TestVector(
                "AES-128 CBC Test Vector 4",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("f69f2445df4f9b17ad2b417be66c3710"),
                AesTestHelpers.hexToBytes("3ff1caa1681fac09120eca307586e1a7"),
                AesTestHelpers.hexToBytes("73bed6b8e3c1743b7116e69e22229516")
            )
        )
    }
    
    /**
     * NIST AES-192 CBC test vectors
     */
    fun getAes192CbcVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-192 CBC Test Vector 1",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("4f021db243bc633d7178183a9fa071e8"),
                AesTestHelpers.hexToBytes("000102030405060708090a0b0c0d0e0f")
            ),
            TestVector(
                "AES-192 CBC Test Vector 2",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("b4d9ada9ad7dedf4e5e738763f69145a"),
                AesTestHelpers.hexToBytes("4f021db243bc633d7178183a9fa071e8")
            )
        )
    }
    
    /**
     * NIST AES-256 CBC test vectors
     */
    fun getAes256CbcVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-256 CBC Test Vector 1",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("f58c4c04d6e5f1ba779eabfb5f7bfbd6"),
                AesTestHelpers.hexToBytes("000102030405060708090a0b0c0d0e0f")
            ),
            TestVector(
                "AES-256 CBC Test Vector 2",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("9cfc4e967edb808d679f777bc6702c7d"),
                AesTestHelpers.hexToBytes("f58c4c04d6e5f1ba779eabfb5f7bfbd6")
            )
        )
    }
    
    /**
     * NIST AES-128 CTR test vectors
     */
    fun getAes128CtrVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-128 CTR Test Vector 1",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("874d6191b620e3261bef6864990db6ce"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff")
            ),
            TestVector(
                "AES-128 CTR Test Vector 2",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("9806f66b7970fdff8617187bb9fffdff"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdff00")
            ),
            TestVector(
                "AES-128 CTR Test Vector 3",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("30c81c46a35ce411e5fbc1191a0a52ef"),
                AesTestHelpers.hexToBytes("5ae4df3edbd5d35e5b4f09020db03eab"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdff01")
            ),
            TestVector(
                "AES-128 CTR Test Vector 4",
                AesTestHelpers.hexToBytes("2b7e151628aed2a6abf7158809cf4f3c"),
                AesTestHelpers.hexToBytes("f69f2445df4f9b17ad2b417be66c3710"),
                AesTestHelpers.hexToBytes("1e031dda2fbe03d1792170a0f3009cee"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdff02")
            )
        )
    }
    
    /**
     * NIST AES-192 CTR test vectors
     */
    fun getAes192CtrVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-192 CTR Test Vector 1",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("1abc932417521ca24f2b0459fe7e6e0b"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff")
            ),
            TestVector(
                "AES-192 CTR Test Vector 2",
                AesTestHelpers.hexToBytes("8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("090339ec0aa6faefd5ccc2c6f4ce8e94"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdff00")
            )
        )
    }
    
    /**
     * NIST AES-256 CTR test vectors
     */
    fun getAes256CtrVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-256 CTR Test Vector 1",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("6bc1bee22e409f96e93d7e117393172a"),
                AesTestHelpers.hexToBytes("601ec313775789a5b7a7f504bbf3d228"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff")
            ),
            TestVector(
                "AES-256 CTR Test Vector 2",
                AesTestHelpers.hexToBytes("603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"),
                AesTestHelpers.hexToBytes("ae2d8a571e03ac9c9eb76fac45af8e51"),
                AesTestHelpers.hexToBytes("f443e3ca4d62b59aca84e990cacaf5c5"),
                AesTestHelpers.hexToBytes("f0f1f2f3f4f5f6f7f8f9fafbfcfdff00")
            )
        )
    }
    
    /**
     * NIST AES-128 GCM test vectors
     */
    fun getAes128GcmVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-128 GCM Test Vector 1",
                AesTestHelpers.hexToBytes("00000000000000000000000000000000"),
                ByteArray(0),
                ByteArray(0),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("58e2fccefa7e3061367f1d57a4e7455a")
            ),
            TestVector(
                "AES-128 GCM Test Vector 2",
                AesTestHelpers.hexToBytes("00000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("00000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("0388dace60b6a392f328c2b971b2fe78"),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("ab6e47d42cec13bdf53a67b21257bddf")
            ),
            TestVector(
                "AES-128 GCM Test Vector 3",
                AesTestHelpers.hexToBytes("feffe9928665731c6d6a8f9467308308"),
                AesTestHelpers.hexToBytes("d9313225f88406e5a55909c5aff5269a86a7a9531534f7da2e4c303d8a318a721c3c0c95956809532fcf0e2449a6b525b16aedf5aa0de657ba637b391aafd255"),
                AesTestHelpers.hexToBytes("42831ec2217774244b7221b784d0d49ce3aa212f2c02a4e035c17e2329aca12e21d514b25466931c7d8f6a5aac84aa051ba30b396a0aac973d58e091473f5985"),
                AesTestHelpers.hexToBytes("cafebabefacedbaddecaf888"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("4d5c2af327cd64a62cf35abd2ba6fab4")
            ),
            TestVector(
                "AES-128 GCM Test Vector 4 (with AAD)",
                AesTestHelpers.hexToBytes("feffe9928665731c6d6a8f9467308308"),
                AesTestHelpers.hexToBytes("d9313225f88406e5a55909c5aff5269a86a7a9531534f7da2e4c303d8a318a721c3c0c95956809532fcf0e2449a6b525b16aedf5aa0de657ba637b39"),
                AesTestHelpers.hexToBytes("42831ec2217774244b7221b784d0d49ce3aa212f2c02a4e035c17e2329aca12e21d514b25466931c7d8f6a5aac84aa051ba30b396a0aac973d58e091"),
                AesTestHelpers.hexToBytes("cafebabefacedbaddecaf888"),
                AesTestHelpers.hexToBytes("feedfacedeadbeeffeedfacedeadbeefabaddad2"),
                AesTestHelpers.hexToBytes("5bc94fbc3221a5db94fae95ae7121a47")
            )
        )
    }
    
    /**
     * NIST AES-192 GCM test vectors
     */
    fun getAes192GcmVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-192 GCM Test Vector 1",
                AesTestHelpers.hexToBytes("000000000000000000000000000000000000000000000000"),
                ByteArray(0),
                ByteArray(0),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("cd33b28ac773f74ba00ed1f312572435")
            ),
            TestVector(
                "AES-192 GCM Test Vector 2",
                AesTestHelpers.hexToBytes("000000000000000000000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("00000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("98e7247c07f0fe411c267e4384b0f600"),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("2ff58d80033927ab8ef4d4587514f0fb")
            ),
            TestVector(
                "AES-192 GCM Test Vector 3 (with AAD)",
                AesTestHelpers.hexToBytes("feffe9928665731c6d6a8f9467308308feffe9928665731c"),
                AesTestHelpers.hexToBytes("d9313225f88406e5a55909c5aff5269a86a7a9531534f7da2e4c303d8a318a721c3c0c95956809532fcf0e2449a6b525b16aedf5aa0de657ba637b39"),
                AesTestHelpers.hexToBytes("3980ca0b3c00e841eb06fac4872a2757859e1ceaa6efd984628593b40ca1e19c7d773d00c144c525ac619d18c84a3f4718e2448b2fe324d9ccda2710"),
                AesTestHelpers.hexToBytes("cafebabefacedbaddecaf888"),
                AesTestHelpers.hexToBytes("feedfacedeadbeeffeedfacedeadbeefabaddad2"),
                AesTestHelpers.hexToBytes("2519498e80f1478f37ba55bd6d27618c")
            )
        )
    }
    
    /**
     * NIST AES-256 GCM test vectors
     */
    fun getAes256GcmVectors(): List<TestVector> {
        return listOf(
            TestVector(
                "AES-256 GCM Test Vector 1",
                AesTestHelpers.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                ByteArray(0),
                ByteArray(0),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("530f8afbc74536b9a963b4f1c4cb738b")
            ),
            TestVector(
                "AES-256 GCM Test Vector 2",
                AesTestHelpers.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("00000000000000000000000000000000"),
                AesTestHelpers.hexToBytes("cea7403d4d606b6e074ec5d3baf39d18"),
                AesTestHelpers.hexToBytes("000000000000000000000000"),
                ByteArray(0),
                AesTestHelpers.hexToBytes("d0d1c8a799996bf0265b98b5d48ab919")
            ),
            TestVector(
                "AES-256 GCM Test Vector 3 (with AAD)",
                AesTestHelpers.hexToBytes("feffe9928665731c6d6a8f9467308308feffe9928665731c6d6a8f9467308308"),
                AesTestHelpers.hexToBytes("d9313225f88406e5a55909c5aff5269a86a7a9531534f7da2e4c303d8a318a721c3c0c95956809532fcf0e2449a6b525b16aedf5aa0de657ba637b39"),
                AesTestHelpers.hexToBytes("522dc1f099567d07f47f37a32a84427d643a8cdcbfe5c0c97598a2bd2555d1aa8cb08e48590dbb3da7b08b1056828838c5f61e6393ba7a0abcc9f662"),
                AesTestHelpers.hexToBytes("cafebabefacedbaddecaf888"),
                AesTestHelpers.hexToBytes("feedfacedeadbeeffeedfacedeadbeefabaddad2"),
                AesTestHelpers.hexToBytes("76fc6ece0f4e1768cddf8853bb2d551b")
            )
        )
    }
}