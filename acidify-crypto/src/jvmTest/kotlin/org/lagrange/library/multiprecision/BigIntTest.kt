package org.lagrange.library.multiprecision

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random

class BigIntTest {
    
    // ============================================================================
    // Construction Tests
    // ============================================================================
    
    @Test
    fun testDefaultConstruction() {
        val n = BigInt()
        assertTrue(n.isZero())
        assertFalse(n.isNegative())
        assertFalse(n.isPositive())
        assertEquals(0, n.bitLength())
        assertEquals(1, n.wordCount())
    }
    
    @Test
    fun testIntegerConstruction() {
        // Positive integers
        val n1 = BigInt(42)
        assertEquals(42, n1.toInt())
        assertFalse(n1.isNegative())
        assertTrue(n1.isPositive())
        
        // Negative integers
        val n2 = BigInt(-42)
        assertEquals(-42, n2.toInt())
        assertTrue(n2.isNegative())
        assertFalse(n2.isPositive())
        
        // Zero
        val n3 = BigInt(0)
        assertTrue(n3.isZero())
        assertFalse(n3.isNegative())
        assertFalse(n3.isPositive())
        
        // Large unsigned
        val n4 = BigInt.fromULong(ULong.MAX_VALUE)
        assertEquals(ULong.MAX_VALUE, n4.toULong())
        assertFalse(n4.isNegative())
        
        // Minimum signed value
        val n5 = BigInt(Long.MIN_VALUE)
        assertEquals(Long.MIN_VALUE, n5.toLong())
        assertTrue(n5.isNegative())
    }
    
    @Test
    fun testStringConstruction() {
        // Decimal strings
        val n1 = BigInt("12345678901234567890")
        assertEquals("12345678901234567890", n1.toString())
        
        val n2 = BigInt("-9876543210987654321")
        assertEquals("-9876543210987654321", n2.toString())
        assertTrue(n2.isNegative())
        
        // Hexadecimal strings
        val n3 = BigInt("0xDEADBEEF", 16)
        assertEquals("deadbeef", n3.toHex())
        
        val n4 = BigInt("0x123456789ABCDEF0123456789ABCDEF0", 16)
        assertEquals("123456789abcdef0123456789abcdef0", n4.toHex())
        
        // Leading zeros should be handled
        val n5 = BigInt("0000042", 10)
        assertEquals(42, n5.toInt())
        
        // Sign prefixes
        val n6 = BigInt("+42", 10)
        assertEquals(42, n6.toInt())
        
        val n7 = BigInt("-0xFF", 16)
        assertEquals(-255, n7.toInt())
    }
    
    @Test
    fun testInvalidStringConstruction() {
        // Invalid characters
        assertThrows<IllegalArgumentException> { BigInt("12x34", 10) }
        assertThrows<IllegalArgumentException> { BigInt("0xGHI", 16) }
        assertThrows<IllegalArgumentException> { BigInt("", 10) }
        
        // Unsupported base
        assertThrows<IllegalArgumentException> { BigInt("123", 3) }
    }
    
    @Test
    fun testByteArrayConstruction() {
        // Big-endian bytes
        val bytes1 = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val n1 = BigInt(bytes1, true)
        assertEquals("1020304", n1.toHex())
        
        // Little-endian bytes
        val n2 = BigInt(bytes1, false)
        assertEquals("4030201", n2.toHex())
        
        // Empty bytes
        val n3 = BigInt(byteArrayOf(), true)
        assertTrue(n3.isZero())
        
        // Large byte array
        val bytes2 = ByteArray(32) { it.toByte() }
        val n4 = BigInt(bytes2, true)
        assertFalse(n4.isZero())
    }
    
    // ============================================================================
    // Arithmetic Tests
    // ============================================================================
    
    @Test
    fun testAddition() {
        // Basic addition
        assertEquals(BigInt(7), BigInt(3) + BigInt(4))
        assertEquals(BigInt(0), BigInt(-5) + BigInt(5))
        
        // Large numbers
        val a = BigInt("123456789012345678901234567890")
        val b = BigInt("987654321098765432109876543210")
        val expected = BigInt("1111111110111111111011111111100")
        assertEquals(expected, a + b)
        
        // Mixed signs
        assertEquals(BigInt(3), BigInt(10) + BigInt(-7))
        assertEquals(BigInt(-3), BigInt(-10) + BigInt(7))
        assertEquals(BigInt(-17), BigInt(-10) + BigInt(-7))
    }
    
    @Test
    fun testSubtraction() {
        // Basic subtraction
        assertEquals(BigInt(3), BigInt(7) - BigInt(4))
        assertEquals(BigInt(-10), BigInt(-5) - BigInt(5))
        
        // Large numbers
        val a = BigInt("987654321098765432109876543210")
        val b = BigInt("123456789012345678901234567890")
        val expected = BigInt("864197532086419753208641975320")
        assertEquals(expected, a - b)
        
        // Mixed signs
        assertEquals(BigInt(17), BigInt(10) - BigInt(-7))
        assertEquals(BigInt(-17), BigInt(-10) - BigInt(7))
        assertEquals(BigInt(-3), BigInt(-10) - BigInt(-7))
    }
    
    @Test
    fun testMultiplication() {
        // Basic multiplication
        assertEquals(BigInt(12), BigInt(3) * BigInt(4))
        assertEquals(BigInt(-20), BigInt(-5) * BigInt(4))
        assertEquals(BigInt(35), BigInt(-5) * BigInt(-7))
        
        // Large numbers
        val a = BigInt("123456789012345678901234567890")
        val b = BigInt("987654321098765432109876543210")
        val expected = BigInt("121932631137021795226185032733622923332237463801111263526900")
        assertEquals(expected, a * b)
        
        // Zero multiplication
        assertEquals(BigInt.ZERO, BigInt(1234567890) * BigInt.ZERO)
        assertEquals(BigInt.ZERO, BigInt.ZERO * BigInt(-987654321))
    }
    
    @Test
    fun testDivision() {
        // Basic division
        assertEquals(BigInt(3), BigInt(12) / BigInt(4))
        assertEquals(BigInt(-4), BigInt(-20) / BigInt(5))
        assertEquals(BigInt(5), BigInt(-35) / BigInt(-7))
        
        // Large numbers
        val a = BigInt("121932631137021795226185032733622923332237463801111263526900")
        val b = BigInt("987654321098765432109876543210")
        val expected = BigInt("123456789012345678901234567890")
        assertEquals(expected, a / b)
        
        // Division by larger number
        assertEquals(BigInt.ZERO, BigInt(5) / BigInt(10))
        
        // Division by zero
        assertThrows<ArithmeticException> { BigInt(10) / BigInt.ZERO }
    }
    
    @Test
    fun testModulo() {
        // Basic modulo
        assertEquals(BigInt(2), BigInt(17) % BigInt(5))
        assertEquals(BigInt(3), BigInt(23) % BigInt(4))
        
        // Negative modulo (mathematical modulo - always positive)
        val result = BigInt(-17) % BigInt(5)
        assertTrue(result.isPositive() || result.isZero())
        assertEquals(BigInt(3), result)  // -17 mod 5 = 3
        
        // Large numbers
        val a = BigInt("123456789012345678901234567890")
        val b = BigInt("9876543210")
        val r = a % b
        assertTrue(r < b)
        assertTrue(r >= BigInt.ZERO)
    }
    
    // ============================================================================
    // Bitwise Operation Tests
    // ============================================================================
    
    @Test
    fun testBitwiseAnd() {
        assertEquals(BigInt(0x0C), BigInt(0x3C) and BigInt(0x0F))
        assertEquals(BigInt(0x20), BigInt(0xFF) and BigInt(0x20))
        
        // Large numbers
        val a = BigInt("0xFF00FF00FF00FF00", 16)
        val b = BigInt("0x0F0F0F0F0F0F0F0F", 16)
        val expected = BigInt("0x0F000F000F000F00", 16)
        assertEquals(expected, a and b)
    }
    
    @Test
    fun testBitwiseOr() {
        assertEquals(BigInt(0x3F), BigInt(0x3C) or BigInt(0x0F))
        assertEquals(BigInt(0xFF), BigInt(0xF0) or BigInt(0x0F))
        
        // Large numbers
        val a = BigInt("0xFF00FF00FF00FF00", 16)
        val b = BigInt("0x00FF00FF00FF00FF", 16)
        val expected = BigInt("0xFFFFFFFFFFFFFFFF", 16)
        assertEquals(expected, a or b)
    }
    
    @Test
    fun testBitwiseXor() {
        assertEquals(BigInt(0x33), BigInt(0x3C) xor BigInt(0x0F))
        assertEquals(BigInt(0xFF), BigInt(0xF0) xor BigInt(0x0F))
        
        // Large numbers
        val a = BigInt("0xFF00FF00FF00FF00", 16)
        val b = BigInt("0x0F0F0F0F0F0F0F0F", 16)
        val expected = BigInt("0xF00FF00FF00FF00F", 16)
        assertEquals(expected, a xor b)
    }
    
    @Test
    fun testShiftOperations() {
        // Left shift
        assertEquals(BigInt(16), BigInt(1) shl 4)
        assertEquals(BigInt(0x1000), BigInt(0x10) shl 8)
        
        // Right shift
        assertEquals(BigInt(1), BigInt(16) shr 4)
        assertEquals(BigInt(0x10), BigInt(0x1000) shr 8)
        
        // Large shifts
        val n = BigInt("0x123456789ABCDEF0", 16)
        assertEquals(BigInt("0x123456789ABCDEF0000000000", 16), n shl 36)
        assertEquals(BigInt("0x123456789AB", 16), n shr 20)
    }
    
    // ============================================================================
    // Comparison Tests
    // ============================================================================
    
    @Test
    fun testComparison() {
        // Basic comparisons
        assertTrue(BigInt(5) < BigInt(10))
        assertTrue(BigInt(10) > BigInt(5))
        assertTrue(BigInt(5) <= BigInt(5))
        assertTrue(BigInt(5) >= BigInt(5))
        assertTrue(BigInt(5) == BigInt(5))
        assertTrue(BigInt(5) != BigInt(10))
        
        // Negative comparisons
        assertTrue(BigInt(-10) < BigInt(-5))
        assertTrue(BigInt(-5) > BigInt(-10))
        assertTrue(BigInt(-10) < BigInt(5))
        assertTrue(BigInt(5) > BigInt(-10))
        
        // Large number comparisons
        val a = BigInt("123456789012345678901234567890")
        val b = BigInt("123456789012345678901234567891")
        assertTrue(a < b)
        assertFalse(a > b)
        assertFalse(a == b)
    }
    
    // ============================================================================
    // Bit Operation Tests
    // ============================================================================
    
    @Test
    fun testBitOperations() {
        val n = BigInt("0xABCDEF", 16)
        
        // Test bit
        assertTrue(n.testBit(0))  // LSB
        assertFalse(n.testBit(4))
        assertTrue(n.testBit(19))
        
        // Set bit
        val n2 = BigInt(n)
        n2.setBit(4, true)
        assertTrue(n2.testBit(4))
        
        n2.setBit(0, false)
        assertFalse(n2.testBit(0))
        
        // Bit length
        assertEquals(24, BigInt("0xABCDEF", 16).bitLength())
        assertEquals(63, BigInt(Long.MAX_VALUE).bitLength())
        assertEquals(0, BigInt.ZERO.bitLength())
    }
    
    // ============================================================================
    // Mathematical Function Tests
    // ============================================================================
    
    @Test
    fun testPower() {
        assertEquals(BigInt(1), BigInt(2).pow(0))
        assertEquals(BigInt(2), BigInt(2).pow(1))
        assertEquals(BigInt(1024), BigInt(2).pow(10))
        assertEquals(BigInt(125), BigInt(5).pow(3))
        
        // Large powers
        val result = BigInt(10).pow(20)
        assertEquals("100000000000000000000", result.toString())
    }
    
    @Test
    fun testGCD() {
        assertEquals(BigInt(6), BigInt(18).gcd(BigInt(24)))
        assertEquals(BigInt(1), BigInt(17).gcd(BigInt(19)))
        assertEquals(BigInt(12), BigInt(144).gcd(BigInt(60)))
        
        // Edge cases
        assertEquals(BigInt(5), BigInt(0).gcd(BigInt(5)))
        assertEquals(BigInt(5), BigInt(5).gcd(BigInt(0)))
        assertEquals(BigInt.ZERO, BigInt(0).gcd(BigInt(0)))
    }
    
    @Test
    fun testModularPower() {
        // Small modular exponentiation
        assertEquals(BigInt(4), BigInt(2).modPow(BigInt(10), BigInt(15)))
        assertEquals(BigInt(1), BigInt(3).modPow(BigInt(4), BigInt(5)))
        
        // Fermat's little theorem test: a^(p-1) ≡ 1 (mod p) for prime p
        val p = BigInt(17)
        val a = BigInt(3)
        assertEquals(BigInt.ONE, a.modPow(p - BigInt.ONE, p))
    }
    
    @Test
    fun testModularInverse() {
        // 3 * 7 ≡ 1 (mod 10)
        assertEquals(BigInt(7), BigInt(3).modInverse(BigInt(10)))
        
        // 5 * 5 ≡ 1 (mod 12)
        assertEquals(BigInt(5), BigInt(5).modInverse(BigInt(12)))
        
        // No inverse exists (not coprime)
        assertThrows<ArithmeticException> { BigInt(4).modInverse(BigInt(6)) }
    }
    
    // ============================================================================
    // Conversion Tests
    // ============================================================================
    
    @Test
    fun testConversions() {
        // To string
        assertEquals("123456789", BigInt(123456789).toString())
        assertEquals("-987654321", BigInt(-987654321).toString())
        assertEquals("0", BigInt.ZERO.toString())
        
        // To hex
        assertEquals("deadbeef", BigInt("0xDEADBEEF", 16).toHex())
        assertEquals("123456789abcdef", BigInt("0x123456789ABCDEF", 16).toHex())
        
        // To bytes
        val n = BigInt("0x0102030405060708", 16)
        val bytesBE = n.toBytes(true)
        assertArrayEquals(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8), bytesBE)
        
        val bytesLE = n.toBytes(false)
        assertArrayEquals(byteArrayOf(8, 7, 6, 5, 4, 3, 2, 1), bytesLE)
    }
    
    // ============================================================================
    // Edge Case Tests
    // ============================================================================
    
    @Test
    fun testEdgeCases() {
        // Operations with zero
        assertEquals(BigInt(5), BigInt(5) + BigInt.ZERO)
        assertEquals(BigInt(5), BigInt(5) - BigInt.ZERO)
        assertEquals(BigInt.ZERO, BigInt(5) * BigInt.ZERO)
        assertThrows<ArithmeticException> { BigInt(5) / BigInt.ZERO }
        
        // Operations with one
        assertEquals(BigInt(5), BigInt(5) * BigInt.ONE)
        assertEquals(BigInt(5), BigInt(5) / BigInt.ONE)
        assertEquals(BigInt.ZERO, BigInt(5) % BigInt.ONE)
        
        // Self operations
        assertEquals(BigInt.ZERO, BigInt(5) - BigInt(5))
        assertEquals(BigInt.ONE, BigInt(5) / BigInt(5))
        assertEquals(BigInt.ZERO, BigInt(5) % BigInt(5))
    }
    
    // ============================================================================
    // Random Property Tests
    // ============================================================================
    
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun testArithmeticProperties(seed: Int) {
        val random = Random(seed)
        
        repeat(100) {
            val a = BigInt(random.nextLong())
            val b = BigInt(random.nextLong())
            val c = BigInt(random.nextLong())
            
            // Commutative property of addition
            assertEquals(a + b, b + a)
            
            // Associative property of addition
            assertEquals((a + b) + c, a + (b + c))
            
            // Commutative property of multiplication
            assertEquals(a * b, b * a)
            
            // Associative property of multiplication
            assertEquals((a * b) * c, a * (b * c))
            
            // Distributive property
            assertEquals(a * (b + c), a * b + a * c)
            
            // Identity properties
            assertEquals(a, a + BigInt.ZERO)
            assertEquals(a, a * BigInt.ONE)
        }
    }
    
    @Test
    fun testLargeNumberOperations() {
        // Test with very large numbers
        val a = BigInt("99999999999999999999999999999999999999999999999999")
        val b = BigInt("11111111111111111111111111111111111111111111111111")
        
        val sum = a + b
        assertEquals("111111111111111111111111111111111111111111111111110", sum.toString())
        
        val diff = a - b
        assertEquals("88888888888888888888888888888888888888888888888888", diff.toString())
        
        val product = a * b
        assertTrue(product > a)
        assertTrue(product > b)
        
        val quotient = a / b
        assertEquals(BigInt(9), quotient)
    }
}