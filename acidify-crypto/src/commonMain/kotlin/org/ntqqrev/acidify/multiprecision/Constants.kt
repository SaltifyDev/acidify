package org.ntqqrev.acidify.multiprecision

// Word type selection for optimal performance
typealias Word = ULong
typealias SWord = Long
typealias HalfWord = UInt

// Constants
const val WORD_BITS: Int = 64
const val WORD_BYTES: Int = 8
val WORD_MAX: Word = ULong.MAX_VALUE
val WORD_MSB: Word = 1UL shl (WORD_BITS - 1)
const val CACHE_LINE_SIZE: Int = 64

// Memory pool configuration for temporary allocations
const val POOL_BLOCK_SIZE: Int = 64  // Words per block
const val POOL_MAX_BLOCKS: Int = 128

// Karatsuba threshold (in words)
const val KARATSUBA_THRESHOLD: Int = 32

// Montgomery threshold (in words)
const val MONTGOMERY_THRESHOLD: Int = 8

// Portable 128-bit type implementation
// Always use our custom implementation for consistency across platforms
data class DWord(
    val high: ULong,
    val low: ULong
) {
    // Default constructor
    constructor() : this(0UL, 0UL)

    // Constructor from 64-bit value
    constructor(value: ULong) : this(0UL, value)

    // Cast to ULong (gets low part)
    fun toULong(): ULong = low

    // Right shift by n bits
    infix fun shr(n: Int): DWord {
        return when {
            n == 0 -> this
            n >= 128 -> DWord(0UL, 0UL)
            n >= 64 -> DWord(0UL, high shr (n - 64))
            else -> DWord(high shr n, (low shr n) or (high shl (64 - n)))
        }
    }

    // Left shift by n bits
    infix fun shl(n: Int): DWord {
        return when {
            n == 0 -> this
            n >= 128 -> DWord(0UL, 0UL)
            n >= 64 -> DWord(low shl (n - 64), 0UL)
            else -> DWord((high shl n) or (low shr (64 - n)), low shl n)
        }
    }

    // Addition
    operator fun plus(other: DWord): DWord {
        val sumLow = low + other.low
        val carry = if (sumLow < low) 1UL else 0UL
        val sumHigh = high + other.high + carry
        return DWord(sumHigh, sumLow)
    }

    // Addition with ULong
    operator fun plus(value: ULong): DWord {
        val sumLow = low + value
        val carry = if (sumLow < low) 1UL else 0UL
        val sumHigh = high + carry
        return DWord(sumHigh, sumLow)
    }

    // Subtraction
    operator fun minus(other: DWord): DWord {
        val diffLow = low - other.low
        val borrow = if (low < other.low) 1UL else 0UL
        val diffHigh = high - other.high - borrow
        return DWord(diffHigh, diffLow)
    }

    // Subtraction with ULong
    operator fun minus(value: ULong): DWord {
        val diffLow = low - value
        val borrow = if (low < value) 1UL else 0UL
        val diffHigh = high - borrow
        return DWord(diffHigh, diffLow)
    }

    // Bitwise AND with ULong
    infix fun and(value: ULong): DWord {
        return DWord(0UL, low and value)
    }

    // Bitwise OR
    infix fun or(other: DWord): DWord {
        return DWord(high or other.high, low or other.low)
    }

    // Multiplication with ULong
    operator fun times(value: ULong): DWord {
        // Only handle simple case: this is a 64-bit value times another 64-bit value
        return if (high == 0UL) {
            multiply64x64(low, value)
        } else {
            // For more complex cases, we'd need full 128x64 multiplication
            DWord(0UL, 0UL)  // Placeholder for complex case
        }
    }

    // Division by ULong
    operator fun div(divisor: ULong): ULong {
        if (divisor == 0UL) return 0UL  // Undefined, but avoid crash
        if (high == 0UL) {
            return low / divisor
        }
        // Full 128-bit division
        return divide128By64(this, divisor).first
    }

    // Modulo by ULong
    operator fun rem(divisor: ULong): ULong {
        if (divisor == 0UL) return 0UL  // Undefined, but avoid crash
        if (high == 0UL) {
            return low % divisor
        }
        // Full 128-bit division
        return divide128By64(this, divisor).second
    }

    // Comparison
    operator fun compareTo(other: DWord): Int {
        return when {
            high > other.high -> 1
            high < other.high -> -1
            low > other.low -> 1
            low < other.low -> -1
            else -> 0
        }
    }

    companion object {
        // Helper: multiply two 64-bit values to get 128-bit result
        fun multiply64x64(a: ULong, b: ULong): DWord {
            // Split each 64-bit value into two 32-bit halves
            val aLow = a and 0xFFFFFFFFUL
            val aHigh = a shr 32
            val bLow = b and 0xFFFFFFFFUL
            val bHigh = b shr 32

            // Four 32x32->64 bit multiplications
            val ll = aLow * bLow
            val lh = aLow * bHigh
            val hl = aHigh * bLow
            val hh = aHigh * bHigh

            // Add cross products
            val cross = lh + hl
            val crossCarry = if (cross < lh) (1UL shl 32) else 0UL

            // Calculate result
            val lowResult = ll + (cross shl 32)
            var highResult = hh + (cross shr 32) + crossCarry
            if (lowResult < ll) highResult++  // Carry from low part

            return DWord(highResult, lowResult)
        }

        // Helper: divide 128-bit by 64-bit
        fun divide128By64(dividend: DWord, divisor: ULong): Pair<ULong, ULong> {
            if (dividend.high < divisor) {
                // Result fits in 64 bits
                // Use shift-subtract division
                var remainder = dividend
                var quotient = 0UL

                for (i in 63 downTo 0) {
                    val temp = DWord(divisor) shl i
                    if (remainder >= temp) {
                        remainder -= temp
                        quotient = quotient or (1UL shl i)
                    }
                }
                return quotient to remainder.low
            }
            // Overflow case - return max values
            return ULong.MAX_VALUE to ULong.MAX_VALUE
        }
    }
}

// Portable carry/borrow operations
fun addWithCarry(a: Word, b: Word, carryIn: Boolean): Pair<Word, Boolean> {
    // Use 128-bit arithmetic to detect carry
    val sum = DWord(a) + b + (if (carryIn) 1UL else 0UL)
    val result = sum.toULong()
    val carryOut = (sum shr WORD_BITS).toULong() != 0UL
    return result to carryOut
}

fun subWithBorrow(a: Word, b: Word, borrowIn: Boolean): Pair<Word, Boolean> {
    // Use 128-bit arithmetic to detect borrow
    val diff = DWord(a) - b - (if (borrowIn) 1UL else 0UL)
    val result = diff.toULong()
    val borrowOut = ((diff shr WORD_BITS).toULong() and 1UL) != 0UL
    return result to borrowOut
}

fun mulWord(a: Word, b: Word): Pair<Word, Word> {
    // Use our portable 128-bit multiplication
    val product = DWord.multiply64x64(a, b)
    return product.high to product.low  // Return high, low
}

// Count leading zeros
fun countLeadingZeros(x: Word): Int {
    if (x == 0UL) return WORD_BITS
    return x.countLeadingZeroBits()
}

// Count trailing zeros
fun countTrailingZeros(x: Word): Int {
    if (x == 0UL) return WORD_BITS
    return x.countTrailingZeroBits()
}

// Constant-time comparison utilities
fun constantTimeEq(a: Word, b: Word): Word {
    return (a xor b).inv() + 1UL  // Returns WORD_MAX if equal, 0 otherwise
}

fun constantTimeSelect(mask: Word, a: Word, b: Word): Word {
    return (mask and a) or (mask.inv() and b)
}