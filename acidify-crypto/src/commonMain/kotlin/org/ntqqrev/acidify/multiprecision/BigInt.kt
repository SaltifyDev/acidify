@file:OptIn(ExperimentalUnsignedTypes::class)

package org.ntqqrev.acidify.multiprecision

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.jvm.JvmStatic
import kotlin.math.max
import kotlin.math.min

/**
 * Memory pool for temporary allocations
 */
internal object MemoryPool {
    private class Block {
        val data = ULongArray(POOL_BLOCK_SIZE)
        val inUse = atomic(false)
    }

    private val blocks = Array(POOL_MAX_BLOCKS) { Block() }
    private val lock = SynchronizedObject()

    fun allocate(words: Int): ULongArray? {
        if (words > POOL_BLOCK_SIZE) {
            return null  // Fall back to regular allocation
        }

        synchronized(lock) {
            for (block in blocks) {
                if (!block.inUse.value) {
                    block.inUse.value = true
                    block.data.fill(0UL)
                    return block.data
                }
            }
        }
        return null
    }

    fun deallocate(ptr: ULongArray) {
        for (block in blocks) {
            if (block.data == ptr) {
                block.inUse.value = false
                return
            }
        }
    }
}

/**
 * RAII wrapper for pool allocations
 */
internal class PoolAllocation(words: Int) {
    private val ptr: ULongArray
    private val size: Int = words
    private val fromPool: Boolean

    init {
        val poolPtr = MemoryPool.allocate(words)
        if (poolPtr != null) {
            ptr = poolPtr
            fromPool = true
        } else {
            ptr = ULongArray(words)
            fromPool = false
        }
    }

    fun data(): ULongArray = ptr
    fun size(): Int = size

    protected fun finalize() {
        if (fromPool) {
            MemoryPool.deallocate(ptr)
        }
    }
}

/**
 * Main BigInteger class
 */
class BigInt : Comparable<BigInt> {
    private var words: MutableList<Word>  // Little-endian storage (least significant word first)
    private var negative: Boolean = false  // Sign flag

    // Remove leading zeros
    private fun normalize() {
        while (words.size > 1 && words.last() == 0UL) {
            words.removeLast()
        }
        if (words.size == 1 && words[0] == 0UL) {
            negative = false  // Zero is always positive
        }
    }

    // Ensure minimum capacity
    private fun ensureCapacity(minWords: Int) {
        if (words is ArrayList) {
            (words as ArrayList).ensureCapacity(minWords + minWords / 4)  // 25% extra
        }
    }

    // Constructors
    constructor() {
        words = mutableListOf(0UL)
        negative = false
    }

    constructor(value: Long) {
        negative = value < 0
        val absValue = if (value == Long.MIN_VALUE) {
            // Special case for minimum value
            value.toULong()
        } else {
            if (negative) (-value).toULong() else value.toULong()
        }
        words = mutableListOf(absValue)
    }

    constructor(value: Int) : this(value.toLong())

    constructor(str: String, base: Int = 10) {
        if (str.isEmpty()) {
            throw IllegalArgumentException("Empty string")
        }

        var start = 0
        negative = false

        when {
            str[0] == '-' -> {
                negative = true
                start = 1
            }

            str[0] == '+' -> {
                start = 1
            }
        }

        if (base == 16 && str.length > start + 2 &&
            str[start] == '0' && (str[start + 1] == 'x' || str[start + 1] == 'X')
        ) {
            start += 2
        }

        // Process string based on base
        if (base == 16) {
            words = mutableListOf()
            val totalHexDigits = str.length - start

            // Process hex string from right to left in chunks
            val hexPerWord = 16  // 16 hex digits per 64-bit word

            var remaining = totalHexDigits
            while (remaining > 0) {
                // Take up to 16 hex digits from the right
                val chunkSize = min(hexPerWord, remaining)
                val chunkStart = start + remaining - chunkSize

                var word = 0UL
                for (i in 0 until chunkSize) {
                    val digit = when (val c = str[chunkStart + i]) {
                        in '0'..'9' -> (c - '0').toULong()
                        in 'a'..'f' -> (c - 'a' + 10).toULong()
                        in 'A'..'F' -> (c - 'A' + 10).toULong()
                        else -> throw IllegalArgumentException("Invalid hex digit: $c")
                    }
                    word = (word shl 4) or digit
                }

                words.add(word)
                remaining -= chunkSize
            }

            if (words.isEmpty()) words.add(0UL)
        } else if (base == 10) {
            // Decimal conversion using repeated multiplication
            words = mutableListOf(0UL)
            var result = BigInt(0)
            val baseVal = BigInt(10)

            for (i in start until str.length) {
                val c = str[i]
                if (c !in '0'..'9') {
                    throw IllegalArgumentException("Invalid decimal digit: $c")
                }
                val digitVal = BigInt(c - '0')
                result = result * baseVal + digitVal
            }
            this.words = result.words.toMutableList()
            // negative flag was already set at the start
        } else {
            throw IllegalArgumentException("Unsupported base: $base")
        }

        normalize()
    }

    constructor(bytes: ByteArray, bigEndian: Boolean = true) {
        if (bytes.isEmpty()) {
            words = mutableListOf(0UL)
            negative = false
            return
        }

        val wordCount = (bytes.size + WORD_BYTES - 1) / WORD_BYTES
        words = MutableList(wordCount) { 0UL }
        negative = false

        if (bigEndian) {
            // Process bytes from end to start for big-endian
            for (i in bytes.indices) {
                val byteIdx = bytes.size - 1 - i
                val wordIdx = i / WORD_BYTES
                val shift = (i % WORD_BYTES) * 8
                words[wordIdx] = words[wordIdx] or (bytes[byteIdx].toUByte().toULong() shl shift)
            }
        } else {
            // Process bytes directly for little-endian
            for (i in bytes.indices) {
                val wordIdx = i / WORD_BYTES
                val shift = (i % WORD_BYTES) * 8
                words[wordIdx] = words[wordIdx] or (bytes[i].toUByte().toULong() shl shift)
            }
        }

        normalize()
    }

    // Copy constructor
    constructor(other: BigInt) {
        words = other.words.toMutableList()
        negative = other.negative
    }

    // Factory methods
    companion object {
        @JvmStatic
        fun fromHex(hex: String): BigInt = BigInt(hex, 16)

        @JvmStatic
        fun fromBytes(bytes: ByteArray, bigEndian: Boolean = true): BigInt =
            BigInt(bytes, bigEndian)

        @JvmStatic
        fun fromULong(value: ULong): BigInt {
            val result = BigInt()
            result.words = mutableListOf(value)
            result.negative = false
            return result
        }

        @JvmStatic
        fun fromUInt(value: UInt): BigInt = fromULong(value.toULong())

        val ZERO = BigInt(0)
        val ONE = BigInt(1)
        val TWO = BigInt(2)
        val TEN = BigInt(10)

        internal fun compareMagnitude(a: BigInt, b: BigInt): Int {
            if (a.words.size != b.words.size) {
                return if (a.words.size < b.words.size) -1 else 1
            }

            for (i in a.words.size - 1 downTo 0) {
                when {
                    a.words[i] < b.words[i] -> return -1
                    a.words[i] > b.words[i] -> return 1
                }
            }

            return 0
        }
    }

    // Accessors
    fun isZero(): Boolean = words.size == 1 && words[0] == 0UL

    fun isOne(): Boolean = !negative && words.size == 1 && words[0] == 1UL

    fun isNegative(): Boolean = negative

    fun isPositive(): Boolean = !negative && !isZero()

    fun isEven(): Boolean = (words[0] and 1UL) == 0UL

    fun isOdd(): Boolean = (words[0] and 1UL) == 1UL

    fun bitLength(): Int {
        if (isZero()) return 0
        val bits = (words.size - 1) * WORD_BITS
        return bits + (WORD_BITS - countLeadingZeros(
            words.last()
        ))
    }

    fun wordCount(): Int = words.size

    // Bit operations
    fun testBit(index: Int): Boolean {
        val wordIdx = index / WORD_BITS
        if (wordIdx >= words.size) return false
        val bitIdx = index % WORD_BITS
        return ((words[wordIdx] shr bitIdx) and 1UL) != 0UL
    }

    fun setBit(index: Int, value: Boolean = true) {
        val wordIdx = index / WORD_BITS
        if (wordIdx >= words.size) {
            if (!value) return  // Setting 0 in non-existent word
            while (words.size <= wordIdx) {
                words.add(0UL)
            }
        }
        val bitIdx = index % WORD_BITS
        if (value) {
            words[wordIdx] = words[wordIdx] or (1UL shl bitIdx)
        } else {
            words[wordIdx] = words[wordIdx] and (1UL shl bitIdx).inv()
        }
        normalize()
    }

    // Conversion methods
    fun toString(base: Int = 10): String {
        if (base != 10 && base != 16) {
            throw IllegalArgumentException("Only base 10 and 16 supported")
        }

        if (isZero()) return "0"

        val result = StringBuilder()

        if (base == 16) {
            // Hexadecimal conversion
            var leading = true
            for (i in words.size - 1 downTo 0) {
                val word = words[i]

                if (leading) {
                    // First word - no leading zeros
                    if (word != 0UL) {
                        result.append(word.toString(16))
                        leading = false
                    }
                } else {
                    // Subsequent words - with leading zeros
                    result.append(word.toString(16).padStart(16, '0'))
                }
            }

            if (result.isEmpty()) result.append("0")
        } else {
            // Decimal conversion
            var temp = this.abs()
            val ten = BigInt(10)

            while (!temp.isZero()) {
                val digit = temp % ten
                result.append('0' + digit.toInt())
                temp = temp / ten
            }

            result.reverse()
        }

        if (negative && !isZero()) {
            result.insert(0, '-')
        }

        return result.toString()
    }

    override fun toString(): String = toString(10)

    fun toHex(): String = toString(16)

    fun toBytes(bigEndian: Boolean = true): ByteArray {
        val result = mutableListOf<Byte>()

        for (word in words) {
            for (i in 0 until WORD_BYTES) {
                result.add(((word shr (i * 8)) and 0xFFUL).toByte())
            }
        }

        // Remove leading zeros
        while (result.isNotEmpty() && result.last() == 0.toByte()) {
            result.removeLast()
        }

        if (result.isEmpty()) {
            result.add(0)
        }

        val bytes = result.toByteArray()
        if (bigEndian) {
            bytes.reverse()
        }

        return bytes
    }

    fun toInt(): Int {
        if (words.isEmpty()) return 0
        val magnitude = words[0].toInt()
        return if (negative) {
            if (words[0] == Long.MIN_VALUE.toULong() && words.size == 1) {
                Int.MIN_VALUE
            } else {
                -magnitude
            }
        } else {
            magnitude
        }
    }

    fun toLong(): Long {
        if (words.isEmpty()) return 0L
        val magnitude = words[0].toLong()
        return if (negative) {
            if (words[0] == Long.MIN_VALUE.toULong() && words.size == 1) {
                Long.MIN_VALUE
            } else {
                -magnitude
            }
        } else {
            magnitude
        }
    }

    fun toULong(): ULong {
        if (words.isEmpty()) return 0UL
        return words[0]
    }

    // Arithmetic operators
    operator fun unaryMinus(): BigInt {
        val result = BigInt(this)
        if (!result.isZero()) {
            result.negative = !result.negative
        }
        return result
    }

    operator fun plus(other: BigInt): BigInt {
        val result = BigInt(this)
        result.plusAssign(other)
        return result
    }

    operator fun minus(other: BigInt): BigInt {
        val result = BigInt(this)
        result.minusAssign(other)
        return result
    }

    operator fun times(other: BigInt): BigInt {
        val result = BigInt(this)
        result.timesAssign(other)
        return result
    }

    operator fun div(other: BigInt): BigInt {
        val result = BigInt(this)
        result.divAssign(other)
        return result
    }

    operator fun rem(other: BigInt): BigInt {
        val result = BigInt(this)
        result.remAssign(other)
        return result
    }

    operator fun plusAssign(other: BigInt) {
        if (negative == other.negative) {
            addMagnitude(other)
        } else {
            val cmp = compareMagnitude(this, other)
            if (cmp >= 0) {
                subMagnitude(other)
            } else {
                val temp = BigInt(other)
                temp.subMagnitude(this)
                this.words = temp.words
                this.negative = other.negative
            }
        }
    }

    operator fun minusAssign(other: BigInt) {
        if (negative != other.negative) {
            addMagnitude(other)
        } else {
            val cmp = compareMagnitude(this, other)
            if (cmp >= 0) {
                subMagnitude(other)
            } else {
                val temp = BigInt(other)
                temp.subMagnitude(this)
                this.words = temp.words
                this.negative = !negative
            }
        }
    }

    operator fun timesAssign(other: BigInt) {
        val resultArray = ULongArray(words.size + other.words.size)
        ArithmeticOps.multiply(resultArray, words.toULongArray(), other.words.toULongArray())
        this.words = resultArray.toMutableList()
        this.negative = negative != other.negative
        this.normalize()
    }

    operator fun divAssign(other: BigInt) {
        val (quotient, _) = ArithmeticOps.divide(this, other)
        this.words = quotient.words
        this.negative = quotient.negative
    }

    operator fun remAssign(other: BigInt) {
        val (_, remainder) = ArithmeticOps.divide(this, other)

        // Ensure the result is always non-negative for mathematical modulo
        // If remainder is negative, add the modulus to get positive result
        var result = remainder
        if (result.negative && !result.isZero()) {
            result = result + other.abs()
        }

        this.words = result.words
        this.negative = result.negative
    }

    // Bitwise operators
    infix fun and(other: BigInt): BigInt {
        val result = BigInt()
        val minSize = min(words.size, other.words.size)
        result.words = MutableList(minSize) { i ->
            words[i] and other.words[i]
        }
        result.normalize()
        return result
    }

    infix fun or(other: BigInt): BigInt {
        val result = BigInt()
        val maxSize = max(words.size, other.words.size)
        result.words = MutableList(maxSize) { 0UL }

        for (i in words.indices) {
            result.words[i] = result.words[i] or words[i]
        }
        for (i in other.words.indices) {
            result.words[i] = result.words[i] or other.words[i]
        }

        result.normalize()
        return result
    }

    infix fun xor(other: BigInt): BigInt {
        val result = BigInt()
        val maxSize = max(words.size, other.words.size)
        result.words = MutableList(maxSize) { 0UL }

        for (i in words.indices) {
            result.words[i] = words[i]
        }
        for (i in other.words.indices) {
            result.words[i] = result.words[i] xor other.words[i]
        }

        result.normalize()
        return result
    }

    fun inv(): BigInt {
        val result = BigInt(this)
        for (i in result.words.indices) {
            result.words[i] = result.words[i].inv()
        }
        result.normalize()
        return result
    }

    // Shift operators
    infix fun shl(shift: Int): BigInt {
        val result = BigInt(this)
        result.shlAssign(shift)
        return result
    }

    infix fun shr(shift: Int): BigInt {
        val result = BigInt(this)
        result.shrAssign(shift)
        return result
    }

    fun shlAssign(shift: Int): BigInt {
        if (shift == 0 || isZero()) return this

        val wordShift = shift / WORD_BITS
        val bitShift = shift % WORD_BITS

        if (wordShift > 0) {
            // Insert zeros at the beginning
            val zeros = MutableList(wordShift) { 0UL }
            words = (zeros + words).toMutableList()
        }

        if (bitShift > 0) {
            var carry = 0UL
            for (i in words.indices) {
                val newCarry = words[i] shr (WORD_BITS - bitShift)
                words[i] = (words[i] shl bitShift) or carry
                carry = newCarry
            }
            if (carry != 0UL) {
                words.add(carry)
            }
        }

        normalize()
        return this
    }

    fun shrAssign(shift: Int): BigInt {
        if (shift == 0 || isZero()) return this

        val wordShift = shift / WORD_BITS
        val bitShift = shift % WORD_BITS

        if (wordShift >= words.size) {
            words = mutableListOf(0UL)
            negative = false
            return this
        }

        if (wordShift > 0) {
            words = words.drop(wordShift).toMutableList()
        }

        if (bitShift > 0) {
            var carry = 0UL
            for (i in words.size - 1 downTo 0) {
                val newCarry = words[i] shl (WORD_BITS - bitShift)
                words[i] = (words[i] shr bitShift) or carry
                carry = newCarry
            }
        }

        normalize()
        return this
    }

    // Increment/Decrement
    operator fun inc(): BigInt {
        val result = this + ONE
        this.words = result.words
        this.negative = result.negative
        return this
    }

    operator fun dec(): BigInt {
        val result = this - ONE
        this.words = result.words
        this.negative = result.negative
        return this
    }

    // Math functions
    fun abs(): BigInt {
        val result = BigInt(this)
        result.negative = false
        return result
    }

    fun pow(exp: Int): BigInt {
        if (exp == 0) return BigInt(1)
        if (exp == 1) return BigInt(this)

        var result = BigInt(1)
        var base = BigInt(this)
        var e = exp

        // Binary exponentiation
        while (e > 0) {
            if (e and 1 == 1) {
                result = result * base
            }
            base = base * base
            e = e shr 1
        }

        return result
    }

    fun modPow(exp: BigInt, mod: BigInt): BigInt {
        return ModularOps.modExp(this, exp, mod)
    }

    fun gcd(other: BigInt): BigInt {
        var a = this.abs()
        var b = other.abs()

        // Binary GCD algorithm
        if (a.isZero()) return b
        if (b.isZero()) return a

        // Find common power of 2
        var shift = 0
        while (a.isEven() && b.isEven()) {
            a = a shr 1
            b = b shr 1
            shift++
        }

        while (!a.isZero()) {
            while (a.isEven()) a = a shr 1
            while (b.isEven()) b = b shr 1

            if (a >= b) {
                a = a - b
            } else {
                b = b - a
            }
        }

        return b shl shift
    }

    fun modInverse(mod: BigInt): BigInt {
        val result = ModularOps.modInverse(this, mod)
        if (result == null) {
            throw ArithmeticException("Modular inverse does not exist")
        }
        return result
    }

    // Comparison operators
    override fun compareTo(other: BigInt): Int {
        if (negative != other.negative) {
            return if (negative) -1 else 1
        }

        val cmp = compareMagnitude(this, other)
        return if (negative) {
            -cmp
        } else {
            cmp
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigInt) return false
        return negative == other.negative && words == other.words
    }

    override fun hashCode(): Int {
        return words.hashCode() * 31 + negative.hashCode()
    }

    // Internal methods
    internal fun getWords(): List<Word> = words
    internal fun getWordsArray(): ULongArray = words.toULongArray()
    internal fun setWords(newWords: List<Word>) {
        words = newWords.toMutableList()
        normalize()
    }

    internal fun setNegative(value: Boolean) {
        negative = value
    }

    private fun addMagnitude(other: BigInt) {
        val maxSize = max(words.size, other.words.size)
        ensureCapacity(maxSize + 1)
        while (words.size < maxSize + 1) {
            words.add(0UL)
        }

        var carry = false
        for (i in 0 until other.words.size) {
            val (result, newCarry) = addWithCarry(
                words[i],
                other.words[i],
                carry
            )
            words[i] = result
            carry = newCarry
        }

        for (i in other.words.size until maxSize) {
            if (!carry) break
            val (result, newCarry) = addWithCarry(
                words[i],
                0UL,
                carry
            )
            words[i] = result
            carry = newCarry
        }

        if (carry) {
            words[maxSize] = 1UL
        }

        normalize()
    }

    private fun subMagnitude(other: BigInt) {
        var borrow = false

        for (i in 0 until other.words.size) {
            val (result, newBorrow) = subWithBorrow(
                words[i],
                other.words[i],
                borrow
            )
            words[i] = result
            borrow = newBorrow
        }

        for (i in other.words.size until words.size) {
            if (!borrow) break
            val (result, newBorrow) = subWithBorrow(
                words[i],
                0UL,
                borrow
            )
            words[i] = result
            borrow = newBorrow
        }

        normalize()
    }

}