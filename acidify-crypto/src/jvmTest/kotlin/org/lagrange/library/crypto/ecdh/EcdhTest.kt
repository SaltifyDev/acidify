package org.lagrange.library.crypto.ecdh

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.lagrange.library.multiprecision.BigInt
import kotlin.system.measureTimeMillis

class EcdhTest {
    
    // ============================================================================
    // EllipticPoint Tests
    // ============================================================================
    
    @Test
    fun testPointConstruction() {
        // Default construction - point at infinity
        val p1 = EllipticPoint()
        assertTrue(p1.isInfinity())
        assertTrue(p1.isDefault())
        assertTrue(p1.x.isZero())
        assertTrue(p1.y.isZero())
        
        // Construction with coordinates
        val x = BigInt(123)
        val y = BigInt(456)
        val p2 = EllipticPoint(x, y)
        assertFalse(p2.isInfinity())
        assertFalse(p2.isDefault())
        assertEquals(BigInt(123), p2.x)
        assertEquals(BigInt(456), p2.y)
        
        // Construction with Long coordinates
        val p3 = EllipticPoint(789L, 101112L)
        assertEquals(BigInt(789), p3.x)
        assertEquals(BigInt(101112), p3.y)
    }
    
    @Test
    fun testPointOperations() {
        // Test point negation
        val x = BigInt(100)
        val y = BigInt(200)
        val p = EllipticPoint(x, y)
        val negP = -p
        
        assertEquals(BigInt(-100), negP.x)
        assertEquals(BigInt(-200), negP.y)
        
        // Test equality comparison
        val p1 = EllipticPoint(BigInt(50), BigInt(60))
        val p2 = EllipticPoint(BigInt(50), BigInt(60))
        val p3 = EllipticPoint(BigInt(50), BigInt(61))
        
        assertEquals(p1, p2)
        assertNotEquals(p1, p3)
    }
    
    @Test
    fun testPointString() {
        // Test string representation
        val inf = EllipticPoint()
        val infStr = inf.toString()
        assertTrue(infStr.contains("Infinity"))
        
        val p = EllipticPoint(BigInt(42), BigInt(84))
        val pStr = p.toString()
        assertTrue(pStr.contains("Point"))
    }
    
    // ============================================================================
    // EllipticCurve Tests
    // ============================================================================
    
    @Test
    fun testSecp192k1Curve() {
        val curve = EllipticCurve.secp192k1()
        
        // Test curve parameters are set
        assertFalse(curve.p.isZero())
        assertTrue(curve.a.isZero())  // secp192k1 has a = 0
        assertEquals(BigInt(3), curve.b)  // secp192k1 has b = 3
        assertFalse(curve.g.isInfinity())
        assertFalse(curve.n.isZero())
        assertEquals(BigInt.ONE, curve.h)
        assertEquals(24, curve.size)
        assertEquals(24, curve.packSize)
        
        // Test that generator point is on the curve
        val onCurve = curve.checkOn(curve.g)
        assertTrue(onCurve)
    }
    
    @Test
    fun testPrime256v1Curve() {
        val curve = EllipticCurve.prime256v1()
        
        // Test curve parameters are set
        assertFalse(curve.p.isZero())
        assertFalse(curve.a.isZero())  // P-256 has a != 0
        assertFalse(curve.b.isZero())  // P-256 has b != 0
        assertFalse(curve.g.isInfinity())
        assertFalse(curve.n.isZero())
        assertEquals(BigInt.ONE, curve.h)
        assertEquals(32, curve.size)
        assertEquals(16, curve.packSize)
        
        // Test that generator point is on the curve
        val onCurve = curve.checkOn(curve.g)
        assertTrue(onCurve)
    }
    
    @Test
    fun testCurvePointValidation() {
        val curve = EllipticCurve.secp192k1()
        
        // Point at infinity should be on the curve
        val inf = EllipticPoint()
        assertTrue(curve.checkOn(inf))
        
        // Random point not on curve
        val randomPoint = EllipticPoint(BigInt(12345), BigInt(67890))
        assertFalse(curve.checkOn(randomPoint))
        
        // Generator should be on curve
        assertTrue(curve.checkOn(curve.g))
    }
    
    // ============================================================================
    // EcdhProvider Tests
    // ============================================================================
    
    @Test
    fun testKeyGeneration() {
        // Test key generation for secp192k1
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Public key should not be at infinity
        val pub = provider.publicPoint
        assertFalse(pub.isInfinity())
        
        // Public key should be on the curve
        assertTrue(curve.checkOn(pub))
    }
    
    @Test
    fun testPublicKeySerialization() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Test compressed format
        val compressed = provider.packPublic(true)
        assertEquals(curve.size + 1, compressed.size)  // 1 byte prefix + x coordinate
        assertTrue(compressed[0] == 0x02.toByte() || compressed[0] == 0x03.toByte())
        
        // Test uncompressed format
        val uncompressed = provider.packPublic(false)
        assertEquals(curve.size * 2 + 1, uncompressed.size)  // 1 byte prefix + x + y
        assertEquals(0x04.toByte(), uncompressed[0])
    }
    
    @Test
    fun testPublicKeyDeserialization() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Pack and unpack compressed public key
        val compressed = provider.packPublic(true)
        val unpackedCompressed = provider.unpackPublic(compressed)
        assertEquals(provider.publicPoint, unpackedCompressed)
        
        // Pack and unpack uncompressed public key
        val uncompressed = provider.packPublic(false)
        val unpackedUncompressed = provider.unpackPublic(uncompressed)
        assertEquals(provider.publicPoint, unpackedUncompressed)
    }
    
    @Test
    fun testSecretKeySerialization() {
        val curve = EllipticCurve.secp192k1()
        val provider1 = EcdhProvider(curve)
        
        // Pack secret
        val secretBytes = provider1.packSecret()
        assertTrue(secretBytes.size >= 4)  // At least 4 bytes header
        
        // Create new provider with same secret
        val provider2 = EcdhProvider(curve, secretBytes)
        
        // Should generate same public key
        assertEquals(provider1.publicPoint, provider2.publicPoint)
    }
    
    @Test
    fun testKeyExchangeSecp192k1() {
        val curve = EllipticCurve.secp192k1()
        
        // Alice generates her key pair
        val alice = EcdhProvider(curve)
        val alicePublic = alice.packPublic(false)
        
        // Bob generates his key pair
        val bob = EcdhProvider(curve)
        val bobPublic = bob.packPublic(false)
        
        // Alice computes shared secret with Bob's public key
        val aliceShared = alice.keyExchange(bobPublic, false)
        assertTrue(aliceShared.isNotEmpty())
        
        // Bob computes shared secret with Alice's public key
        val bobShared = bob.keyExchange(alicePublic, false)
        assertTrue(bobShared.isNotEmpty())
        
        // Both should compute the same shared secret
        assertArrayEquals(aliceShared, bobShared)
    }
    
    @Test
    fun testKeyExchangeWithHash() {
        val curve = EllipticCurve.secp192k1()
        
        // Alice generates her key pair
        val alice = EcdhProvider(curve)
        val alicePublic = alice.packPublic(true)  // Use compressed format
        
        // Bob generates his key pair
        val bob = EcdhProvider(curve)
        val bobPublic = bob.packPublic(true)  // Use compressed format
        
        // Key exchange with hash option
        val aliceShared = alice.keyExchange(bobPublic, true)
        val bobShared = bob.keyExchange(alicePublic, true)
        
        // Both should compute the same shared secret (even with hash)
        assertArrayEquals(aliceShared, bobShared)
        
        // Hashed output should be MD5 size (16 bytes)
        assertEquals(16, aliceShared.size)
    }
    
    @Test
    fun testMultipleKeyExchanges() {
        val curve = EllipticCurve.secp192k1()
        
        // Create three parties
        val alice = EcdhProvider(curve)
        val bob = EcdhProvider(curve)
        val charlie = EcdhProvider(curve)
        
        val alicePub = alice.packPublic(false)
        val bobPub = bob.packPublic(false)
        val charliePub = charlie.packPublic(false)
        
        // Alice-Bob exchange
        val aliceBobShared = alice.keyExchange(bobPub, false)
        val bobAliceShared = bob.keyExchange(alicePub, false)
        assertArrayEquals(aliceBobShared, bobAliceShared)
        
        // Alice-Charlie exchange
        val aliceCharlieShared = alice.keyExchange(charliePub, false)
        val charlieAliceShared = charlie.keyExchange(alicePub, false)
        assertArrayEquals(aliceCharlieShared, charlieAliceShared)
        
        // Bob-Charlie exchange
        val bobCharlieShared = bob.keyExchange(charliePub, false)
        val charlieBobShared = charlie.keyExchange(bobPub, false)
        assertArrayEquals(bobCharlieShared, charlieBobShared)
        
        // All three shared secrets should be different
        assertFalse(aliceBobShared.contentEquals(aliceCharlieShared))
        assertFalse(aliceBobShared.contentEquals(bobCharlieShared))
        assertFalse(aliceCharlieShared.contentEquals(bobCharlieShared))
    }
    
    @Test
    fun testPointAddition() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Test point at infinity as identity
        val inf = EllipticPoint()
        val p = EllipticPoint(BigInt(100), BigInt(200))
        
        val result1 = provider.pointAdd(inf, p)
        assertEquals(p, result1)
        
        val result2 = provider.pointAdd(p, inf)
        assertEquals(p, result2)
        
        // Test adding point to itself (point doubling)
        val g = curve.g
        val doubled = provider.pointAdd(g, g)
        assertFalse(doubled.isInfinity())
        assertNotEquals(doubled, g)
    }
    
    @Test
    fun testScalarMultiplication() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Test multiplication by 1 (should return same point)
        val result1 = provider.createShared(BigInt.ONE, curve.g)
        assertEquals(curve.g, result1)
        
        // Test multiplication by 2 (should be same as point doubling)
        val result2 = provider.createShared(BigInt.TWO, curve.g)
        val doubled = provider.pointAdd(curve.g, curve.g)
        assertEquals(doubled, result2)
        
        // Test multiplication by 0 (should return infinity)
        val result0 = provider.createShared(BigInt.ZERO, curve.g)
        assertTrue(result0.isInfinity())
        
        // Test multiplication by curve order (should return infinity)
        val resultN = provider.createShared(curve.n, curve.g)
        assertTrue(resultN.isInfinity())
    }
    
    @Test
    fun testConsistentKeyGeneration() {
        val curve = EllipticCurve.secp192k1()
        
        // Generate a key pair and save the secret
        val provider1 = EcdhProvider(curve)
        val secret = provider1.packSecret()
        val public1 = provider1.packPublic(false)
        
        // Create multiple providers with the same secret
        val provider2 = EcdhProvider(curve, secret)
        val provider3 = EcdhProvider(curve, secret)
        
        val public2 = provider2.packPublic(false)
        val public3 = provider3.packPublic(false)
        
        // All should produce the same public key
        assertArrayEquals(public1, public2)
        assertArrayEquals(public2, public3)
    }
    
    @Test
    fun testInvalidPublicKey() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Test invalid public key format (wrong prefix)
        val invalidKey = ByteArray(curve.size + 1)
        invalidKey[0] = 0x05  // Invalid prefix
        
        assertThrows<IllegalArgumentException> {
            provider.unpackPublic(invalidKey)
        }
        
        // Test invalid key length
        val shortKey = ByteArray(5)
        shortKey[0] = 0x04
        
        assertThrows<IllegalArgumentException> {
            provider.unpackPublic(shortKey)
        }
    }
    
    @Test
    fun testCompressedKeyRoundtrip() {
        val curve = EllipticCurve.secp192k1()
        
        // Generate multiple key pairs and test compressed format roundtrip
        repeat(5) {
            val provider = EcdhProvider(curve)
            
            // Get original public point
            val original = provider.publicPoint
            
            // Compress, then decompress
            val compressed = provider.packPublic(true)
            val decompressed = provider.unpackPublic(compressed)
            
            // Should match original
            assertEquals(original.x, decompressed.x)
            assertEquals(original.y, decompressed.y)
        }
    }
    
    // ============================================================================
    // Edge Cases and Error Handling
    // ============================================================================
    
    @Test
    fun testEdgeCases() {
        val curve = EllipticCurve.secp192k1()
        val provider = EcdhProvider(curve)
        
        // Test with point at infinity
        val inf = EllipticPoint()
        val result = provider.createShared(BigInt(123), inf)
        assertTrue(result.isInfinity())
        
        // Test negative scalar
        val g = curve.g
        val posResult = provider.createShared(BigInt(5), g)
        val negResult = provider.createShared(BigInt(-5), g)
        
        // Negative scalar should work (handled internally)
        assertFalse(posResult.isInfinity())
        assertFalse(negResult.isInfinity())
    }
    
    // ============================================================================
    // Main ECDH Module Tests
    // ============================================================================
    
    @Test
    fun testEcdhModule() {
        // Test convenience curve references
        assertNotNull(Ecdh.Secp192K1)
        assertNotNull(Ecdh.Prime256V1)
        
        // Test key pair generation
        val keyPair = Ecdh.generateKeyPair()
        assertNotNull(keyPair)
        assertFalse(keyPair.publicPoint.isInfinity())
        
        // Test key exchange using module functions
        val alice = Ecdh.generateKeyPair(Ecdh.Secp192K1)
        val bob = Ecdh.generateKeyPair(Ecdh.Secp192K1)
        
        val aliceShared = Ecdh.keyExchange(alice, bob.packPublic(false))
        val bobShared = Ecdh.keyExchange(bob, alice.packPublic(false))
        
        assertArrayEquals(aliceShared, bobShared)
    }
    
    // ============================================================================
    // Performance Tests (Optional)
    // ============================================================================
    
    @Test
    fun testKeyGenerationPerformance() {
        val curve = EllipticCurve.secp192k1()
        
        val duration = measureTimeMillis {
            // Generate 10 key pairs
            repeat(10) {
                val provider = EcdhProvider(curve)
                val pub = provider.packPublic(false)
                assertNotNull(pub)  // Use the variable
            }
        }
        
        println("Generated 10 key pairs in $duration ms")
        
        // Just ensure it completes in reasonable time (< 5 seconds)
        assertTrue(duration < 5000)
    }
    
    @Test
    fun testKeyExchangePerformance() {
        val curve = EllipticCurve.secp192k1()
        
        val alice = EcdhProvider(curve)
        val bob = EcdhProvider(curve)
        
        val alicePub = alice.packPublic(false)
        val bobPub = bob.packPublic(false)
        
        val duration = measureTimeMillis {
            // Perform 10 key exchanges
            repeat(10) {
                val shared = alice.keyExchange(bobPub, false)
                assertNotNull(shared)  // Use the variable
            }
        }
        
        println("Performed 10 key exchanges in $duration ms")
        
        // Just ensure it completes in reasonable time (< 5 seconds)
        assertTrue(duration < 5000)
    }
}