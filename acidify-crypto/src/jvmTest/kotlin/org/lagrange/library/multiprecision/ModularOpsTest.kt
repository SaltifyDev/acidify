package org.lagrange.library.multiprecision

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ModularOpsTest {
    
    @Test
    fun testModularAddition() {
        val mod = BigInt(17)
        
        // Basic modular addition
        assertEquals(BigInt(7), ModularOps.modAdd(BigInt(3), BigInt(4), mod))
        assertEquals(BigInt(3), ModularOps.modAdd(BigInt(10), BigInt(10), mod))
        assertEquals(BigInt(16), ModularOps.modAdd(BigInt(15), BigInt(18), mod))
    }
    
    @Test
    fun testModularSubtraction() {
        val mod = BigInt(17)
        
        // Basic modular subtraction
        assertEquals(BigInt(6), ModularOps.modSub(BigInt(10), BigInt(4), mod))
        assertEquals(BigInt(13), ModularOps.modSub(BigInt(3), BigInt(7), mod))
        assertEquals(BigInt(0), ModularOps.modSub(BigInt(17), BigInt(17), mod))
    }
    
    @Test
    fun testModularMultiplication() {
        val mod = BigInt(17)
        
        // Basic modular multiplication
        assertEquals(BigInt(12), ModularOps.modMul(BigInt(3), BigInt(4), mod))
        assertEquals(BigInt(16), ModularOps.modMul(BigInt(5), BigInt(10), mod))
        assertEquals(BigInt(0), ModularOps.modMul(BigInt(17), BigInt(5), mod))
        
        // Large modular multiplication
        val largeMod = BigInt("1000000007")  // Common prime modulus
        val a = BigInt("999999999")
        val b = BigInt("888888888")
        val result = ModularOps.modMul(a, b, largeMod)
        assertTrue(result < largeMod)
    }
    
    @Test
    fun testModularExponentiation() {
        // Small exponentiation
        assertEquals(BigInt(1), ModularOps.modExp(BigInt(2), BigInt(0), BigInt(5)))
        assertEquals(BigInt(2), ModularOps.modExp(BigInt(2), BigInt(1), BigInt(5)))
        assertEquals(BigInt(4), ModularOps.modExp(BigInt(2), BigInt(2), BigInt(5)))
        assertEquals(BigInt(3), ModularOps.modExp(BigInt(2), BigInt(3), BigInt(5)))
        
        // Fermat's little theorem: a^(p-1) ≡ 1 (mod p)
        val primes = listOf(3, 5, 7, 11, 13, 17, 19, 23, 29, 31)
        for (p in primes) {
            val mod = BigInt(p)
            for (a in 2 until p) {
                val result = ModularOps.modExp(BigInt(a), mod - BigInt.ONE, mod)
                assertEquals(BigInt.ONE, result, "Failed for a=$a, p=$p")
            }
        }
        
        // Large exponentiation
        val mod = BigInt("1000000007")
        val base = BigInt("2")
        val exp = BigInt("1000000")
        val result = ModularOps.modExp(base, exp, mod)
        assertTrue(result < mod)
    }
    
    @Test
    fun testExtendedGCD() {
        // gcd(10, 6) = 2 = 10*(-1) + 6*2
        val (gcd1, x1, y1) = ModularOps.extendedGcd(BigInt(10), BigInt(6))
        assertEquals(BigInt(2), gcd1)
        assertEquals(BigInt(2), BigInt(10) * x1 + BigInt(6) * y1)
        
        // gcd(35, 15) = 5 = 35*1 + 15*(-2)
        val (gcd2, x2, y2) = ModularOps.extendedGcd(BigInt(35), BigInt(15))
        assertEquals(BigInt(5), gcd2)
        assertEquals(BigInt(5), BigInt(35) * x2 + BigInt(15) * y2)
        
        // Coprime numbers
        val (gcd3, x3, y3) = ModularOps.extendedGcd(BigInt(17), BigInt(13))
        assertEquals(BigInt.ONE, gcd3)
        assertEquals(BigInt.ONE, BigInt(17) * x3 + BigInt(13) * y3)
    }
    
    @Test
    fun testModularInverse() {
        // 3 * 7 ≡ 1 (mod 10)
        assertEquals(BigInt(7), ModularOps.modInverse(BigInt(3), BigInt(10)))
        
        // 5 * 5 ≡ 1 (mod 12)
        assertEquals(BigInt(5), ModularOps.modInverse(BigInt(5), BigInt(12)))
        
        // 7 * 8 ≡ 1 (mod 11)
        assertEquals(BigInt(8), ModularOps.modInverse(BigInt(7), BigInt(11)))
        
        // No inverse (not coprime)
        assertNull(ModularOps.modInverse(BigInt(4), BigInt(6)))
        assertNull(ModularOps.modInverse(BigInt(10), BigInt(15)))
        
        // Verify inverse property
        val mod = BigInt(17)
        for (a in 1..16) {
            val inv = ModularOps.modInverse(BigInt(a), mod)
            assertNotNull(inv)
            assertEquals(BigInt.ONE, ModularOps.modMul(BigInt(a), inv!!, mod))
        }
    }
    
    @Test
    fun testChineseRemainderTheorem() {
        // x ≡ 2 (mod 3), x ≡ 3 (mod 5), x ≡ 2 (mod 7)
        val remainders1 = listOf(BigInt(2), BigInt(3), BigInt(2))
        val moduli1 = listOf(BigInt(3), BigInt(5), BigInt(7))
        val result1 = ModularOps.chineseRemainder(remainders1, moduli1)
        assertNotNull(result1)
        assertEquals(BigInt(2), result1!! % BigInt(3))
        assertEquals(BigInt(3), result1 % BigInt(5))
        assertEquals(BigInt(2), result1 % BigInt(7))
        
        // x ≡ 1 (mod 2), x ≡ 2 (mod 3), x ≡ 3 (mod 5)
        val remainders2 = listOf(BigInt(1), BigInt(2), BigInt(3))
        val moduli2 = listOf(BigInt(2), BigInt(3), BigInt(5))
        val result2 = ModularOps.chineseRemainder(remainders2, moduli2)
        assertNotNull(result2)
        assertEquals(BigInt(23), result2)
        
        // Non-coprime moduli (should fail)
        val remainders3 = listOf(BigInt(1), BigInt(2))
        val moduli3 = listOf(BigInt(4), BigInt(6))  // gcd(4, 6) = 2
        val result3 = ModularOps.chineseRemainder(remainders3, moduli3)
        assertNull(result3)
    }
    
    @Test
    fun testModularSquareRoot() {
        // Test for p ≡ 3 (mod 4)
        val p1 = BigInt(7)
        assertEquals(BigInt(4), ModularOps.modSqrt(BigInt(2), p1))  // 4^2 ≡ 2 (mod 7)
        
        val p2 = BigInt(11)
        assertEquals(BigInt(5), ModularOps.modSqrt(BigInt(3), p2))  // 5^2 ≡ 3 (mod 11)
        
        // Test for p ≡ 1 (mod 4) using Tonelli-Shanks
        val p3 = BigInt(13)
        val sqrt1 = ModularOps.modSqrt(BigInt(10), p3)
        assertNotNull(sqrt1)
        assertEquals(BigInt(10), ModularOps.modMul(sqrt1!!, sqrt1, p3))
        
        // No square root exists
        val p4 = BigInt(7)
        assertNull(ModularOps.modSqrt(BigInt(3), p4))
        
        // Square root of 0 is 0
        assertEquals(BigInt.ZERO, ModularOps.modSqrt(BigInt.ZERO, BigInt(17)))
    }
    
    @Test
    fun testJacobiSymbol() {
        // Test known Jacobi symbols
        assertEquals(BigInt.ONE, ModularOps.computeJacobi(BigInt(2), BigInt(7)))
        assertEquals(BigInt(-1), ModularOps.computeJacobi(BigInt(2), BigInt(3)))
        assertEquals(BigInt.ONE, ModularOps.computeJacobi(BigInt(4), BigInt(7)))
        assertEquals(BigInt.ZERO, ModularOps.computeJacobi(BigInt(15), BigInt(5)))
        
        // Quadratic reciprocity tests
        assertEquals(BigInt(-1), ModularOps.computeJacobi(BigInt(3), BigInt(7)))
        assertEquals(BigInt(-1), ModularOps.computeJacobi(BigInt(7), BigInt(11)))
    }
    
    @Test
    fun testMontgomeryForm() {
        val mod = BigInt(17)
        val mont = MontgomeryForm(mod)
        
        // Test conversion to/from Montgomery form
        val a = BigInt(5)
        val aMont = mont.toMontgomery(a)
        assertEquals(a, mont.fromMontgomery(aMont))
        
        // Test Montgomery multiplication
        val b = BigInt(7)
        val bMont = mont.toMontgomery(b)
        val productMont = mont.montgomeryMultiply(aMont, bMont)
        val product = mont.fromMontgomery(productMont)
        assertEquals(BigInt.ONE, product)  // 5 * 7 = 35 ≡ 1 (mod 17)
        
        // Test modular exponentiation
        val base = BigInt(3)
        val exp = BigInt(10)
        val expected = ModularOps.modExp(base, exp, mod)
        val result = mont.modExp(base, exp)
        assertEquals(expected, result)
    }
    
    @Test
    fun testBarrettReduction() {
        val mod = BigInt(100)
        val barrett = BarrettReduction(mod)
        
        // Test basic reduction
        assertEquals(BigInt(34), barrett.reduce(BigInt(1234)))
        assertEquals(BigInt(56), barrett.reduce(BigInt(456)))
        assertEquals(BigInt(99), barrett.reduce(BigInt(99)))
        assertEquals(BigInt(0), barrett.reduce(BigInt(100)))
        
        // Test large number reduction
        val largeMod = BigInt("1000000007")
        val largeBarrett = BarrettReduction(largeMod)
        val x = BigInt("123456789012345678901234567890")
        val reduced = largeBarrett.reduce(x)
        assertTrue(reduced < largeMod)
        assertEquals(x % largeMod, reduced)
    }
    
    @ParameterizedTest
    @ValueSource(ints = [3, 5, 7, 11, 13, 17, 19, 23, 29, 31])
    fun testModularArithmeticProperties(p: Int) {
        val mod = BigInt(p)
        
        // Test that modular operations preserve field properties
        for (a in 1 until p) {
            for (b in 1 until p) {
                val aBI = BigInt(a)
                val bBI = BigInt(b)
                
                // Addition is closed
                val sum = ModularOps.modAdd(aBI, bBI, mod)
                assertTrue(sum >= BigInt.ZERO && sum < mod)
                
                // Multiplication is closed
                val product = ModularOps.modMul(aBI, bBI, mod)
                assertTrue(product >= BigInt.ZERO && product < mod)
                
                // Inverse exists for non-zero elements
                val inv = ModularOps.modInverse(aBI, mod)
                assertNotNull(inv)
                assertEquals(BigInt.ONE, ModularOps.modMul(aBI, inv!!, mod))
            }
        }
    }
}