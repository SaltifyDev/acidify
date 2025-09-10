@file:OptIn(ExperimentalUnsignedTypes::class)

package org.ntqqrev.acidify.multiprecision

import kotlin.math.max
import kotlin.math.min

/**
 * Arithmetic operations for BigInt
 */
internal object ArithmeticOps {

    // Core addition of magnitudes (ignoring signs)
    fun addMagnitudes(
        result: ULongArray,
        a: ULongArray,
        b: ULongArray
    ) {
        val minSize = min(a.size, b.size)
        val maxSize = max(a.size, b.size)

        // Ensure result has enough space
        if (result.size < maxSize + 1) return

        var carry = false
        var i = 0

        // Add common parts with carry propagation
        while (i < minSize) {
            val (sum, newCarry) = addWithCarry(a[i], b[i], carry)
            result[i] = sum
            carry = newCarry
            i++
        }

        // Copy remaining part with carry propagation
        val longer = if (a.size > b.size) a else b
        while (i < maxSize) {
            val (sum, newCarry) = addWithCarry(
                longer[i],
                0UL,
                carry
            )
            result[i] = sum
            carry = newCarry
            i++
        }

        // Final carry
        if (i < result.size) {
            result[i] = if (carry) 1UL else 0UL
        }
    }

    // Core subtraction of magnitudes (a >= b required)
    fun subMagnitudes(
        result: ULongArray,
        a: ULongArray,
        b: ULongArray
    ): Boolean {
        if (b.size > a.size) return false

        var borrow = false
        var i = 0

        // Subtract common parts
        while (i < b.size) {
            val (diff, newBorrow) = subWithBorrow(
                a[i],
                b[i],
                borrow
            )
            result[i] = diff
            borrow = newBorrow
            i++
        }

        // Handle remaining part with borrow
        while (i < a.size) {
            val (diff, newBorrow) = subWithBorrow(
                a[i],
                0UL,
                borrow
            )
            result[i] = diff
            borrow = newBorrow
            i++
        }

        return !borrow  // Should be no final borrow if a >= b
    }

    // Multiplication using Karatsuba algorithm for large numbers
    fun multiply(
        result: ULongArray,
        a: ULongArray,
        b: ULongArray
    ) {
        val aSize = a.size
        val bSize = b.size

        // Clear result
        result.fill(0UL)

        if (aSize == 0 || bSize == 0) return

        // Use Karatsuba for large numbers
        if (aSize >= KARATSUBA_THRESHOLD && bSize >= KARATSUBA_THRESHOLD) {
            karatsubaMultiply(result, a, b)
        } else {
            schoolbookMultiply(result, a, b)
        }
    }

    // Classical O(n²) multiplication for small numbers
    fun schoolbookMultiply(
        result: ULongArray,
        a: ULongArray,
        b: ULongArray
    ) {
        for (i in a.indices) {
            if (a[i] == 0UL) continue  // Skip zero multiplications

            var carry = 0UL
            for (j in b.indices) {
                val (high, low) = mulWord(a[i], b[j])

                // Add low to result[i+j] with carry
                val (r1, c1) = addWithCarry(
                    result[i + j],
                    low,
                    false
                )
                val (r2, c2) = addWithCarry(r1, carry, false)
                result[i + j] = r2
                carry = high + (if (c1) 1UL else 0UL) + (if (c2) 1UL else 0UL)
            }

            // Propagate final carry
            if (i + b.size < result.size) {
                result[i + b.size] += carry
            }
        }
    }

    // Karatsuba multiplication for large numbers
    fun karatsubaMultiply(
        result: ULongArray,
        a: ULongArray,
        b: ULongArray
    ) {
        val n = max(a.size, b.size)
        val half = n / 2

        if (n < KARATSUBA_THRESHOLD) {
            schoolbookMultiply(result, a, b)
            return
        }

        // Split numbers: a = a1*B^m + a0, b = b1*B^m + b0
        val a0 = a.sliceArray(0 until min(half, a.size))
        val a1 = if (a.size > half) a.sliceArray(half until a.size) else ulongArrayOf()
        val b0 = b.sliceArray(0 until min(half, b.size))
        val b1 = if (b.size > half) b.sliceArray(half until b.size) else ulongArrayOf()

        // Allocate temporary storage
        val z0 = ULongArray(2 * half)
        val z1 = ULongArray(2 * (n - half))
        val z2 = ULongArray(2 * half + 2)

        // z0 = a0 * b0
        if (a0.isNotEmpty() && b0.isNotEmpty()) {
            karatsubaMultiply(z0, a0, b0)
        }

        // z1 = a1 * b1
        if (a1.isNotEmpty() && b1.isNotEmpty()) {
            karatsubaMultiply(z1, a1, b1)
        }

        // z2 = (a0 + a1) * (b0 + b1) - z0 - z1
        val sumA = ULongArray(half + 1)
        val sumB = ULongArray(half + 1)

        addMagnitudes(sumA, a0, a1)
        addMagnitudes(sumB, b0, b1)
        karatsubaMultiply(z2, sumA, sumB)

        // z2 = z2 - z0 - z1
        subMagnitudes(z2, z2, z0)
        subMagnitudes(z2, z2, z1)

        // Combine results: result = z1*B^(2m) + z2*B^m + z0
        z0.copyInto(result, 0, 0, z0.size)

        // Add z2 shifted by half
        var carry = 0UL
        for (i in z2.indices) {
            if (i + half >= result.size) break
            val (sum, c) = addWithCarry(
                result[i + half],
                z2[i],
                carry != 0UL
            )
            result[i + half] = sum
            carry = if (c) 1UL else 0UL
        }

        // Add z1 shifted by 2*half
        carry = 0UL
        for (i in z1.indices) {
            if (i + 2 * half >= result.size) break
            val (sum, c) = addWithCarry(
                result[i + 2 * half],
                z1[i],
                carry != 0UL
            )
            result[i + 2 * half] = sum
            carry = if (c) 1UL else 0UL
        }
    }

    // Division using Knuth's Algorithm D
    fun divide(dividend: BigInt, divisor: BigInt): Pair<BigInt, BigInt> {
        if (divisor.isZero()) {
            throw ArithmeticException("Division by zero")
        }

        // Handle signs
        val negResult = dividend.isNegative() != divisor.isNegative()

        val a = dividend.abs()
        val b = divisor.abs()

        if (BigInt.compareMagnitude(a, b) < 0) {
            return BigInt.ZERO to dividend
        }

        val quotient: BigInt
        val remainder: BigInt

        if (b.wordCount() == 1) {
            // Single word divisor - use optimized division
            val (q, r) = divideByWord(a, b.getWords()[0])
            quotient = q
            remainder = r
        } else {
            // Multi-word divisor - use Knuth's Algorithm D
            val (q, r) = knuthDivide(a, b)
            quotient = q
            remainder = r
        }

        if (negResult && !quotient.isZero()) {
            quotient.setNegative(true)
        }
        if (dividend.isNegative() && !remainder.isZero()) {
            remainder.setNegative(true)
        }

        return quotient to remainder
    }

    // Optimized division by single word
    private fun divideByWord(dividend: BigInt, divisor: Word): Pair<BigInt, BigInt> {
        val quotient = BigInt()
        val quotientWords = MutableList(dividend.wordCount()) { 0UL }
        var rem = 0UL

        val dividendWords = dividend.getWords()

        for (i in dividend.wordCount() - 1 downTo 0) {
            val temp = DWord(rem, dividendWords[i])
            quotientWords[i] = temp / divisor
            rem = temp % divisor
        }

        quotient.setWords(quotientWords)
        return quotient to BigInt.fromULong(rem)
    }

    // Knuth's Algorithm D for multi-word division
    private fun knuthDivide(dividend: BigInt, divisor: BigInt): Pair<BigInt, BigInt> {
        val n = divisor.wordCount()
        val m = dividend.wordCount() - n

        // Normalize divisor
        val shift = countLeadingZeros(divisor.getWords().last())
        val normDivisor = divisor shl shift
        val normDividend = dividend shl shift

        // Ensure dividend has extra word for algorithm
        val normDividendWords = normDividend.getWords().toMutableList()
        normDividendWords.add(0UL)

        // Create quotient words directly - don't use setWords yet to avoid normalization
        val quotientWords = MutableList(m + 1) { 0UL }

        val normDivisorWords = normDivisor.getWords()

        for (j in m downTo 0) {
            // Calculate trial quotient
            val temp = DWord(
                normDividendWords[j + n],
                normDividendWords[j + n - 1]
            )
            var qHat = temp / normDivisorWords[n - 1]
            var rHat = temp % normDivisorWords[n - 1]

            // Adjust q_hat if needed
            while (qHat == WORD_MAX ||
                (n >= 2 && DWord(qHat) * normDivisorWords[n - 2] >
                        DWord(rHat, normDividendWords[j + n - 2]))
            ) {
                qHat--
                rHat += normDivisorWords[n - 1]
                if (rHat < normDivisorWords[n - 1]) break
            }

            // Multiply and subtract
            var carry = 0UL
            var borrow = false

            for (k in 0 until n) {
                val (high, low) = mulWord(
                    qHat,
                    normDivisorWords[k]
                )

                val (r1, b1) = subWithBorrow(
                    normDividendWords[j + k],
                    low,
                    borrow
                )
                val (r2, b2) = subWithBorrow(r1, carry, false)
                normDividendWords[j + k] = r2
                borrow = b1 || b2
                carry = high
            }

            val (finalResult, finalBorrow) = subWithBorrow(
                normDividendWords[j + n],
                carry,
                borrow
            )
            normDividendWords[j + n] = finalResult
            borrow = finalBorrow

            quotientWords[j] = qHat

            // Add back if we subtracted too much
            if (borrow) {
                quotientWords[j]--
                carry = 0UL
                for (k in 0 until n) {
                    val (sum, c) = addWithCarry(
                        normDividendWords[j + k],
                        normDivisorWords[k],
                        carry != 0UL
                    )
                    normDividendWords[j + k] = sum
                    carry = if (c) 1UL else 0UL
                }
                normDividendWords[j + n] += carry
            }
        }

        // Denormalize remainder
        val remainder = BigInt()
        remainder.setWords(normDividendWords.take(n))
        val denormalizedRemainder = remainder shr shift

        // Create quotient from the quotientWords
        val quotient = BigInt()
        quotient.setWords(quotientWords)
        return quotient to denormalizedRemainder
    }

    // Square operation (optimized multiplication)
    fun square(result: ULongArray, a: ULongArray) {
        val n = a.size
        result.fill(0UL)

        // Compute cross products
        for (i in 0 until n) {
            var carry = 0UL

            // Cross products (j < i)
            for (j in 0 until i) {
                val (high, low) = mulWord(a[i], a[j])

                // Double the product for cross terms
                val (doubleLow, c1) = addWithCarry(
                    low,
                    low,
                    false
                )
                val doubleHigh = (high shl 1) or (if (c1) 1UL else 0UL)

                // Add to result
                val (r, c2) = addWithCarry(
                    result[i + j],
                    doubleLow,
                    carry != 0UL
                )
                result[i + j] = r
                carry = doubleHigh + (if (c2) 1UL else 0UL)
            }

            // Square term (i == j)
            val (high, low) = mulWord(a[i], a[i])
            val (r, c3) = addWithCarry(
                result[2 * i],
                low,
                carry != 0UL
            )
            result[2 * i] = r
            carry = high + (if (c3) 1UL else 0UL)

            // Propagate carry
            var k = 2 * i + 1
            while (k < result.size && carry != 0UL) {
                val (sum, c) = addWithCarry(
                    result[k],
                    0UL,
                    carry != 0UL
                )
                result[k] = sum
                carry = if (c) 1UL else 0UL
                k++
            }
        }
    }
}