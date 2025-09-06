@file:OptIn(ExperimentalUnsignedTypes::class)

package org.lagrange.library.multiprecision

import kotlin.random.Random

/**
 * Prime testing algorithms
 */
object PrimeTest {
    // Small primes for trial division
    private val smallPrimes = ulongArrayOf(
        2UL, 3UL, 5UL, 7UL, 11UL, 13UL, 17UL, 19UL, 23UL, 29UL, 31UL, 37UL, 41UL, 43UL, 47UL,
        53UL, 59UL, 61UL, 67UL, 71UL, 73UL, 79UL, 83UL, 89UL, 97UL, 101UL, 103UL, 107UL, 109UL, 113UL,
        127UL, 131UL, 137UL, 139UL, 149UL, 151UL, 157UL, 163UL, 167UL, 173UL, 179UL, 181UL, 191UL, 193UL, 197UL,
        199UL, 211UL, 223UL, 227UL, 229UL, 233UL, 239UL, 241UL, 251UL, 257UL, 263UL, 269UL, 271UL, 277UL, 281UL,
        283UL, 293UL, 307UL, 311UL, 313UL, 317UL, 331UL, 337UL, 347UL, 349UL, 353UL, 359UL, 367UL, 373UL, 379UL,
        383UL, 389UL, 397UL, 401UL, 409UL, 419UL, 421UL, 431UL, 433UL, 439UL, 443UL, 449UL, 457UL, 461UL, 463UL,
        467UL, 479UL, 487UL, 491UL, 499UL, 503UL, 509UL, 521UL, 523UL, 541UL
    )

    // Test bases for deterministic Miller-Rabin
    private val testBases = ulongArrayOf(
        2UL, 3UL, 5UL, 7UL, 11UL, 13UL, 17UL, 19UL, 23UL, 29UL, 31UL, 37UL, 41UL, 43UL, 47UL,
        53UL, 59UL, 61UL, 67UL, 71UL, 73UL, 79UL, 83UL, 89UL, 97UL
    )

    // Quick divisibility check by small primes
    fun hasSmallFactor(n: BigInt): Boolean {
        if (n <= BigInt.ONE) return true

        for (p in smallPrimes) {
            if (n == BigInt(p.toLong())) return false  // n is a small prime
            if ((n % BigInt(p.toLong())).isZero()) return true  // n has small factor
        }

        return false
    }

    // Miller-Rabin primality test
    fun millerRabin(n: BigInt, k: Int = 20): Boolean {
        if (n <= BigInt.ONE) return false
        if (n == BigInt.TWO) return true
        if (n.isEven()) return false

        // Check if n is one of the small primes
        for (p in smallPrimes) {
            if (n == BigInt(p.toLong())) return true
        }

        // Check if n has small factors (composite)
        if (hasSmallFactor(n)) {
            return false
        }

        // Write n-1 as 2^r * d
        var d = n - BigInt.ONE
        var r = 0
        while (d.isEven()) {
            d = d shr 1
            r++
        }

        // Perform k rounds of testing with deterministic witnesses
        for (i in 0 until minOf(k, testBases.size)) {
            val a = BigInt(testBases[i].toLong())

            // Skip if a >= n
            if (a >= n) continue

            var x = ModularOps.modExp(a, d, n)

            if (x == BigInt.ONE || x == n - BigInt.ONE) {
                continue
            }

            var composite = true
            for (j in 0 until r - 1) {
                x = ModularOps.modMul(x, x, n)
                if (x == n - BigInt.ONE) {
                    composite = false
                    break
                }
            }

            if (composite) {
                return false
            }
        }

        return true
    }

    // Lucas-Lehmer test (for Mersenne numbers)
    fun lucasLehmer(p: Int): Boolean {
        if (p == 2) return true
        if (!millerRabin(BigInt(p), 3)) return false

        val m = (BigInt.ONE shl p) - BigInt.ONE  // 2^p - 1
        var s = BigInt(4)

        for (i in 0 until p - 2) {
            s = (s * s - BigInt.TWO) % m
        }

        return s.isZero()
    }

    // Solovay-Strassen primality test
    fun solovayStrassen(n: BigInt, k: Int = 20): Boolean {
        if (n <= BigInt.ONE) return false
        if (n == BigInt.TWO) return true
        if (n.isEven()) return false

        for (i in 0 until minOf(k, testBases.size)) {
            val a = BigInt(testBases[i].toLong())

            // Skip if a >= n
            if (a >= n) continue

            val jacobi = ModularOps.computeJacobi(a, n)

            if (jacobi.isZero()) return false

            val expResult = ModularOps.modExp(a, (n - BigInt.ONE) / BigInt.TWO, n)

            // Convert Jacobi symbol to mod n representation
            val jacobiMod = if (jacobi == BigInt(-1)) n - BigInt.ONE else jacobi

            if (expResult != jacobiMod) {
                return false
            }
        }

        return true
    }
}

/**
 * Elliptic curve point operations helper
 */
class ECPoint {
    private val x: BigInt
    private val y: BigInt
    private val isInfinity: Boolean

    // Constructors
    constructor() {
        x = BigInt.ZERO
        y = BigInt.ZERO
        isInfinity = true
    }

    constructor(x: BigInt, y: BigInt) {
        this.x = x
        this.y = y
        isInfinity = false
    }

    fun isInfinity(): Boolean = isInfinity
    fun x(): BigInt = x
    fun y(): BigInt = y

    // Point doubling for y^2 = x^3 + ax + b
    fun doublePoint(a: BigInt, p: BigInt): ECPoint {
        if (isInfinity) return this

        // λ = (3x^2 + a) / (2y)
        val numerator = ModularOps.modAdd(
            ModularOps.modMul(BigInt(3), ModularOps.modMul(x, x, p), p),
            a, p
        )
        val denominator = ModularOps.modMul(BigInt.TWO, y, p)
        val lambda = ModularOps.modMul(numerator, denominator.modInverse(p), p)

        // x3 = λ^2 - 2x
        val x3 = ModularOps.modSub(
            ModularOps.modMul(lambda, lambda, p),
            ModularOps.modMul(BigInt.TWO, x, p),
            p
        )

        // y3 = λ(x - x3) - y
        val y3 = ModularOps.modSub(
            ModularOps.modMul(lambda, ModularOps.modSub(x, x3, p), p),
            y, p
        )

        return ECPoint(x3, y3)
    }

    // Point addition
    fun add(other: ECPoint, p: BigInt): ECPoint {
        if (isInfinity) return other
        if (other.isInfinity) return this

        if (x == other.x) {
            return if (y == other.y) {
                // Point doubling
                doublePoint(BigInt.ZERO, p)  // Assuming a=0 for simplicity
            } else {
                // Points are inverses
                ECPoint()
            }
        }

        // λ = (y2 - y1) / (x2 - x1)
        val numerator = ModularOps.modSub(other.y, y, p)
        val denominator = ModularOps.modSub(other.x, x, p)
        val lambda = ModularOps.modMul(numerator, denominator.modInverse(p), p)

        // x3 = λ^2 - x1 - x2
        val x3 = ModularOps.modSub(
            ModularOps.modSub(
                ModularOps.modMul(lambda, lambda, p),
                x, p
            ),
            other.x, p
        )

        // y3 = λ(x1 - x3) - y1
        val y3 = ModularOps.modSub(
            ModularOps.modMul(lambda, ModularOps.modSub(x, x3, p), p),
            y, p
        )

        return ECPoint(x3, y3)
    }

    // Scalar multiplication using double-and-add
    fun scalarMult(k: BigInt, a: BigInt, p: BigInt): ECPoint {
        if (k.isZero() || isInfinity) return ECPoint()

        var result = ECPoint()  // Point at infinity
        var addend = this
        var n = BigInt(k)

        while (!n.isZero()) {
            if (n.isOdd()) {
                result = result.add(addend, p)
            }
            addend = addend.doublePoint(a, p)
            n = n shr 1
        }

        return result
    }
}

/**
 * Random number generation utilities
 */
object RandomUtils {

    /**
     * Generate a random BigInt with specified number of bits
     */
    fun randomBits(bits: Int, random: Random = Random.Default): BigInt {
        val words = (bits + WORD_BITS - 1) / WORD_BITS
        val wordList = mutableListOf<ULong>()

        for (i in 0 until words) {
            wordList.add(random.nextLong().toULong())
        }

        // Adjust the last word to have exactly the right number of bits
        val excessBits = words * WORD_BITS - bits
        if (excessBits > 0 && wordList.isNotEmpty()) {
            wordList[wordList.size - 1] = wordList[wordList.size - 1] shr excessBits
        }

        // Ensure the high bit is set for exactly 'bits' bits
        if (bits > 0 && wordList.isNotEmpty()) {
            val lastWordBits = bits % WORD_BITS
            val bitPos = if (lastWordBits == 0) WORD_BITS - 1 else lastWordBits - 1
            wordList[wordList.size - 1] = wordList[wordList.size - 1] or (1UL shl bitPos)
        }

        val result = BigInt()
        result.setWords(wordList)
        return result
    }

    /**
     * Generate a random BigInt in range [min, max]
     */
    fun randomRange(min: BigInt, max: BigInt, random: Random = Random.Default): BigInt {
        require(min <= max) { "min must be less than or equal to max" }

        // Special case: if min == max, return that value
        if (min == max) {
            return min
        }

        val range = max - min + BigInt.ONE
        val bits = range.bitLength()

        // Generate random numbers until we get one in range
        while (true) {
            val candidate = randomBits(bits, random)
            if (candidate < range) {
                return min + candidate
            }
        }
    }

    /**
     * Generate a random prime with specified number of bits
     */
    fun randomPrime(bits: Int, random: Random = Random.Default): BigInt {
        require(bits >= 2) { "bits must be at least 2" }

        while (true) {
            // Generate odd random number with specified bits
            var candidate = randomBits(bits, random)
            if (candidate.isEven()) {
                candidate = candidate or BigInt.ONE
            }

            // Test for primality
            if (PrimeTest.millerRabin(candidate, 20)) {
                return candidate
            }
        }
    }

    /**
     * Generate a safe prime (p where (p-1)/2 is also prime)
     */
    fun randomSafePrime(bits: Int, random: Random = Random.Default): BigInt {
        require(bits >= 3) { "bits must be at least 3" }

        while (true) {
            // Generate candidate for Sophie Germain prime q
            val q = randomPrime(bits - 1, random)
            val p = q * BigInt.TWO + BigInt.ONE

            // Check if p is also prime
            if (PrimeTest.millerRabin(p, 20)) {
                return p
            }
        }
    }
}