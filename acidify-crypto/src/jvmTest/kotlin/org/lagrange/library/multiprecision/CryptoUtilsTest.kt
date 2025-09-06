package org.lagrange.library.multiprecision

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

class CryptoUtilsTest {
    
    // Known small primes for testing
    private val smallPrimes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
        53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113
    )
    
    // Known composite numbers
    private val composites = listOf(
        4, 6, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 24, 25,
        26, 27, 28, 30, 32, 33, 34, 35, 36, 38, 39, 40, 42, 44, 45
    )
    
    @Test
    fun testHasSmallFactor() {
        // Test that small primes don't have small factors
        for (p in smallPrimes) {
            assertFalse(PrimeTest.hasSmallFactor(BigInt(p)),
                "Prime $p should not have small factors")
        }
        
        // Test that composites have small factors
        for (c in composites) {
            assertTrue(PrimeTest.hasSmallFactor(BigInt(c)),
                "Composite $c should have small factors")
        }
        
        // Edge cases
        assertTrue(PrimeTest.hasSmallFactor(BigInt.ZERO))
        assertTrue(PrimeTest.hasSmallFactor(BigInt.ONE))
        assertFalse(PrimeTest.hasSmallFactor(BigInt.TWO))
    }
    
    @Test
    fun testMillerRabin() {
        // Test small primes
        for (p in smallPrimes) {
            assertTrue(PrimeTest.millerRabin(BigInt(p)),
                "Miller-Rabin should identify $p as prime")
        }
        
        // Test composites
        for (c in composites) {
            assertFalse(PrimeTest.millerRabin(BigInt(c)),
                "Miller-Rabin should identify $c as composite")
        }
        
        // Test larger known primes
        val largePrimes = listOf(
            "1000000007",
            "1000000009",
            "1000000021",
            "1000000033"
        )
        for (p in largePrimes) {
            assertTrue(PrimeTest.millerRabin(BigInt(p)),
                "Miller-Rabin should identify $p as prime")
        }
        
        // Test Carmichael numbers (pseudoprimes)
        val carmichaelNumbers = listOf(561, 1105, 1729, 2465, 2821)
        for (c in carmichaelNumbers) {
            assertFalse(PrimeTest.millerRabin(BigInt(c), 20),
                "Miller-Rabin should identify Carmichael number $c as composite")
        }
        
        // Edge cases
        assertFalse(PrimeTest.millerRabin(BigInt.ZERO))
        assertFalse(PrimeTest.millerRabin(BigInt.ONE))
        assertTrue(PrimeTest.millerRabin(BigInt.TWO))
        assertFalse(PrimeTest.millerRabin(BigInt(-5)))
    }
    
    @Test
    fun testSolovayStrassen() {
        // Test small primes
        for (p in smallPrimes.take(10)) {  // Test fewer for performance
            assertTrue(PrimeTest.solovayStrassen(BigInt(p)),
                "Solovay-Strassen should identify $p as prime")
        }
        
        // Test composites
        for (c in composites.take(10)) {
            assertFalse(PrimeTest.solovayStrassen(BigInt(c)),
                "Solovay-Strassen should identify $c as composite")
        }
        
        // Edge cases
        assertFalse(PrimeTest.solovayStrassen(BigInt.ZERO))
        assertFalse(PrimeTest.solovayStrassen(BigInt.ONE))
        assertTrue(PrimeTest.solovayStrassen(BigInt.TWO))
    }
    
    @Test
    fun testLucasLehmer() {
        // Test known Mersenne primes (2^p - 1 where p is in this list)
        val mersennePrimeExponents = listOf(2, 3, 5, 7, 13, 17, 19)
        
        for (p in mersennePrimeExponents) {
            assertTrue(PrimeTest.lucasLehmer(p),
                "Lucas-Lehmer should confirm 2^$p - 1 is prime")
        }
        
        // Test known non-Mersenne exponents
        val nonMersenneExponents = listOf(4, 6, 8, 9, 10, 11, 12, 14, 15, 16)
        
        for (p in nonMersenneExponents) {
            if (PrimeTest.millerRabin(BigInt(p), 3)) {
                assertFalse(PrimeTest.lucasLehmer(p),
                    "Lucas-Lehmer should confirm 2^$p - 1 is composite")
            }
        }
    }
    
    @Test
    fun testECPointOperations() {
        // Test on curve y^2 = x^3 + 7 (mod 17)
        val p = BigInt(17)
        val a = BigInt.ZERO
        
        // Test point addition
        val p1 = ECPoint(BigInt(6), BigInt(11))
        val p2 = ECPoint(BigInt(10), BigInt(2))
        val sum = p1.add(p2, p)
        assertFalse(sum.isInfinity())
        
        // Test point doubling
        val doubled = p1.doublePoint(a, p)
        assertFalse(doubled.isInfinity())
        
        // Test point at infinity
        val inf = ECPoint()
        assertTrue(inf.isInfinity())
        
        val p3 = ECPoint(BigInt(5), BigInt(1))
        val sumWithInf = p3.add(inf, p)
        assertEquals(p3.x(), sumWithInf.x())
        assertEquals(p3.y(), sumWithInf.y())
        
        // Test scalar multiplication
        val point = ECPoint(BigInt(6), BigInt(11))
        val scalar = BigInt(5)
        val result = point.scalarMult(scalar, a, p)
        assertFalse(result.isInfinity())
    }
    
    @Test
    fun testRandomBits() {
        val random = Random(42)
        
        // Test various bit sizes
        val bitSizes = listOf(8, 16, 32, 64, 128, 256)
        
        for (bits in bitSizes) {
            val n = RandomUtils.randomBits(bits, random)
            val actualBits = n.bitLength()
            assertEquals(bits, actualBits,
                "Random number should have exactly $bits bits")
        }
        
        // Test that high bit is set
        for (i in 1..10) {
            val n = RandomUtils.randomBits(100, random)
            assertEquals(100, n.bitLength())
            assertTrue(n.testBit(99))  // High bit should be set
        }
    }
    
    @Test
    fun testRandomRange() {
        val random = Random(42)
        
        // Test small range
        val min1 = BigInt(10)
        val max1 = BigInt(20)
        for (i in 1..100) {
            val n = RandomUtils.randomRange(min1, max1, random)
            assertTrue(n >= min1)
            assertTrue(n <= max1)
        }
        
        // Test large range
        val min2 = BigInt("1000000000000000000")
        val max2 = BigInt("9999999999999999999")
        for (i in 1..10) {
            val n = RandomUtils.randomRange(min2, max2, random)
            assertTrue(n >= min2)
            assertTrue(n <= max2)
        }
        
        // Test edge case: min == max
        val value = BigInt(42)
        val n = RandomUtils.randomRange(value, value, random)
        assertEquals(value, n)
    }
    
    @Test
    fun testRandomPrime() {
        val random = Random(42)
        
        // Test generating primes of various sizes
        val bitSizes = listOf(8, 16, 32, 64)
        
        for (bits in bitSizes) {
            val prime = RandomUtils.randomPrime(bits, random)
            
            // Check it has the right size
            assertEquals(bits, prime.bitLength(),
                "Prime should have exactly $bits bits")
            
            // Check it's actually prime
            assertTrue(PrimeTest.millerRabin(prime, 20),
                "Generated number should be prime")
            
            // Check it's odd (except for 2)
            if (prime != BigInt.TWO) {
                assertTrue(prime.isOdd())
            }
        }
    }
    
    @Test
    fun testRandomSafePrime() {
        val random = Random(42)
        
        // Generate a small safe prime for testing
        val safePrime = RandomUtils.randomSafePrime(16, random)
        
        // Check p is prime
        assertTrue(PrimeTest.millerRabin(safePrime, 20))
        
        // Check (p-1)/2 is also prime (Sophie Germain prime)
        val sophieGermain = (safePrime - BigInt.ONE) / BigInt.TWO
        assertTrue(PrimeTest.millerRabin(sophieGermain, 20))
        
        // Check size
        assertTrue(safePrime.bitLength() >= 15)  // Allow some variance
        assertTrue(safePrime.bitLength() <= 16)
    }
    
    @Test
    fun testPrimeTestConsistency() {
        // Test that different primality tests agree on small numbers
        for (n in 2..100) {
            val num = BigInt(n)
            val millerRabinResult = PrimeTest.millerRabin(num)
            val solovayStrassenResult = PrimeTest.solovayStrassen(num)
            
            assertEquals(millerRabinResult, solovayStrassenResult,
                "Primality tests disagree on $n")
        }
    }
    
    @Test
    fun testLargePrimes() {
        // Test with known large primes (first few primes over 2^64)
        val largePrimes = listOf(
            "18446744073709551629",  // First prime > 2^64
            "18446744073709551653",  // Second prime > 2^64
            "18446744073709551667"   // Third prime > 2^64
        )
        
        for (p in largePrimes) {
            val prime = BigInt(p)
            assertTrue(PrimeTest.millerRabin(prime, 20),
                "$p should be identified as prime")
        }
        
        // Test with known large composites
        val largeComposites = listOf(
            "18446744073709551615",  // 2^64 - 1 = 3 × 5 × 17 × 257 × 641 × 65537 × 6700417
            "18446744073709551614"   // 2^64 - 2 = 2 × 9223372036854775807
        )
        
        for (c in largeComposites) {
            val composite = BigInt(c)
            assertFalse(PrimeTest.millerRabin(composite, 20),
                "$c should be identified as composite")
        }
    }
}