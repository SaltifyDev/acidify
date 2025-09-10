package org.ntqqrev.acidify.multiprecision

/**
 * Montgomery form representation for fast modular arithmetic
 */
class MontgomeryForm(private val modulus: BigInt) {
    private val r: BigInt        // R = 2^k where k = bit_length(modulus)
    private val rInv: BigInt      // R^(-1) mod modulus
    private val r2: BigInt        // R^2 mod modulus
    private val nPrime: Word      // -modulus^(-1) mod 2^WORD_BITS
    private val k: Int            // Bit length of modulus

    init {
        require(modulus.isOdd() && modulus > BigInt.ONE) {
            "Modulus must be odd and greater than 1"
        }

        k = modulus.bitLength()
        val wordCount = (k + WORD_BITS - 1) / WORD_BITS

        // R = 2^(word_count * WORD_BITS)
        r = BigInt.ONE shl (wordCount * WORD_BITS)

        // Compute R^2 mod modulus
        r2 = (r * r) % modulus

        // Compute R^(-1) mod modulus using extended GCD
        rInv = r.modInverse(modulus)

        // Compute n' = -n^(-1) mod 2^WORD_BITS
        nPrime = computeNPrime(modulus.getWords()[0])
    }

    // Compute n' = -n^(-1) mod 2^WORD_BITS
    private fun computeNPrime(n: Word): Word {
        // Use Newton's method to compute modular inverse
        var x = n
        x = x * (2UL - n * x)
        x = x * (2UL - n * x)
        x = x * (2UL - n * x)
        x = x * (2UL - n * x)
        x = x * (2UL - n * x)
        return (0UL - x)
    }

    // Convert to Montgomery form: a_mont = a * R mod modulus
    fun toMontgomery(a: BigInt): BigInt {
        return montgomeryMultiply(a % modulus, r2)
    }

    // Convert from Montgomery form: a = a_mont * R^(-1) mod modulus
    fun fromMontgomery(aMont: BigInt): BigInt {
        return montgomeryReduce(aMont)
    }

    // Montgomery multiplication: result = a * b * R^(-1) mod modulus
    fun montgomeryMultiply(a: BigInt, b: BigInt): BigInt {
        val product = a * b
        return montgomeryReduce(product)
    }

    // Montgomery reduction: result = a * R^(-1) mod modulus
    fun montgomeryReduce(a: BigInt): BigInt {
        // Simple approach: a * R^(-1) mod modulus = (a * r_inv) mod modulus
        return (a * rInv) % modulus
    }

    // Modular exponentiation in Montgomery form
    fun modExp(base: BigInt, exp: BigInt): BigInt {
        var baseMont = toMontgomery(base)
        var resultMont = toMontgomery(BigInt.ONE)

        // Binary exponentiation in Montgomery form
        for (i in 0 until exp.bitLength()) {
            if (exp.testBit(i)) {
                resultMont = montgomeryMultiply(resultMont, baseMont)
            }
            if (i < exp.bitLength() - 1) {
                baseMont = montgomeryMultiply(baseMont, baseMont)
            }
        }

        return fromMontgomery(resultMont)
    }
}

/**
 * Barrett reduction for fast modular reduction
 */
class BarrettReduction(private val modulus: BigInt) {
    private val mu: BigInt        // Precomputed value for reduction
    private val k: Int            // k = ceil(log2(modulus))

    init {
        require(modulus > BigInt.ZERO) {
            "Modulus must be positive"
        }

        k = modulus.bitLength()

        // mu = floor(2^(2k) / modulus)
        val power = BigInt.ONE shl (2 * k)
        mu = power / modulus
    }

    // Barrett reduction: result = x mod modulus
    fun reduce(x: BigInt): BigInt {
        if (x < modulus) return x

        // Barrett reduction algorithm
        val xBits = x.bitLength()
        val maxBits = 2 * k

        // If input is too large, use regular modulo operation
        if (xBits > maxBits) {
            return x % modulus
        }

        // Step 1: Estimate quotient using precomputed mu
        val q1 = x shr (k - 1)
        val q2 = q1 * mu
        val q3 = q2 shr (k + 1)

        // Step 2: Compute remainder
        // r = x - q3 * modulus
        var r = x - (q3 * modulus)

        // Step 3: Final reduction
        // At most 2 subtractions are needed
        while (r >= modulus) {
            r = r - modulus
        }

        // Handle negative results (shouldn't happen, but be safe)
        while (r.isNegative()) {
            r = r + modulus
        }

        return r
    }
}

/**
 * Main modular arithmetic operations
 */
object ModularOps {

    // Basic modular operations
    fun modAdd(a: BigInt, b: BigInt, mod: BigInt): BigInt {
        return (a + b) % mod
    }

    fun modSub(a: BigInt, b: BigInt, mod: BigInt): BigInt {
        val result = (a - b) % mod
        return if (result.isNegative()) {
            result + mod
        } else {
            result
        }
    }

    fun modMul(a: BigInt, b: BigInt, mod: BigInt): BigInt {
        return if (mod.wordCount() >= MONTGOMERY_THRESHOLD && mod.isOdd()) {
            // Use Montgomery multiplication for large odd moduli
            val mont = MontgomeryForm(mod)
            val aMont = mont.toMontgomery(a)
            val bMont = mont.toMontgomery(b)
            val resultMont = mont.montgomeryMultiply(aMont, bMont)
            mont.fromMontgomery(resultMont)
        } else {
            // Use Barrett reduction for general case
            val barrett = BarrettReduction(mod)
            barrett.reduce(a * b)
        }
    }

    // Modular exponentiation using binary method
    fun modExp(base: BigInt, exp: BigInt, mod: BigInt): BigInt {
        if (mod == BigInt.ONE) return BigInt.ZERO
        if (exp.isZero()) return BigInt.ONE

        // Use Montgomery form for large odd moduli
        if (mod.wordCount() >= MONTGOMERY_THRESHOLD && mod.isOdd()) {
            val mont = MontgomeryForm(mod)
            return mont.modExp(base, exp)
        }

        // Binary exponentiation with Barrett reduction
        val barrett = BarrettReduction(mod)
        var result = BigInt.ONE
        var b = base % mod
        var e = BigInt(exp)

        while (!e.isZero()) {
            if (e.isOdd()) {
                result = barrett.reduce(result * b)
            }
            b = barrett.reduce(b * b)
            e = e shr 1
        }

        return result
    }

    // Extended Euclidean algorithm: returns (gcd, x, y) where gcd = a*x + b*y
    fun extendedGcd(a: BigInt, b: BigInt): Triple<BigInt, BigInt, BigInt> {
        if (b.isZero()) {
            return Triple(a, BigInt.ONE, BigInt.ZERO)
        }

        var x0 = BigInt.ONE
        var x1 = BigInt.ZERO
        var y0 = BigInt.ZERO
        var y1 = BigInt.ONE
        var aCopy = BigInt(a)
        var bCopy = BigInt(b)

        while (!bCopy.isZero()) {
            val q = aCopy / bCopy

            val newA = bCopy
            bCopy = aCopy - q * bCopy
            aCopy = newA

            val newX = x0 - q * x1
            x0 = x1
            x1 = newX

            val newY = y0 - q * y1
            y0 = y1
            y1 = newY
        }

        return Triple(aCopy, x0, y0)
    }

    // Modular multiplicative inverse
    fun modInverse(a: BigInt, mod: BigInt): BigInt? {
        val (gcd, x, _) = extendedGcd(a % mod, mod)

        if (!gcd.isOne()) {
            return null  // No inverse exists
        }

        // Ensure positive result
        var result = x % mod
        if (result.isNegative()) {
            result = result + mod
        }

        return result
    }

    // Chinese Remainder Theorem
    // Solves system: x ≡ a_i (mod m_i) for coprime m_i
    fun chineseRemainder(
        remainders: List<BigInt>,
        moduli: List<BigInt>
    ): BigInt? {

        if (remainders.size != moduli.size || remainders.isEmpty()) {
            return null
        }

        var result = remainders[0]
        var prod = moduli[0]

        for (i in 1 until remainders.size) {
            val invOpt = modInverse(prod, moduli[i])
            if (invOpt == null) {
                return null  // Moduli not coprime
            }

            val inv = invOpt
            result = result + prod * (((remainders[i] - result) * inv) % moduli[i])
            prod = prod * moduli[i]
            result = result % prod
        }

        return result
    }

    // Tonelli-Shanks algorithm for square root modulo prime
    fun modSqrt(a: BigInt, p: BigInt): BigInt? {
        if (a.isZero()) return BigInt.ZERO

        // Check if square root exists using Legendre symbol
        val legendre = modExp(a, (p - BigInt.ONE) / BigInt.TWO, p)
        if (legendre != BigInt.ONE) {
            return null  // No square root exists
        }

        // Special case: p ≡ 3 (mod 4)
        if ((p % BigInt(4)) == BigInt(
                3
            )
        ) {
            return modExp(a, (p + BigInt.ONE) / BigInt(
                4
            ), p)
        }

        // Tonelli-Shanks algorithm
        var s = BigInt.ZERO
        var q = p - BigInt.ONE
        while (q.isEven()) {
            q = q shr 1
            s = s + BigInt.ONE
        }

        // Find quadratic non-residue
        var z = BigInt.TWO
        while (modExp(z, (p - BigInt.ONE) / BigInt.TWO, p) != p - BigInt.ONE) {
            z = z + BigInt.ONE
        }

        var m = s
        var c = modExp(z, q, p)
        var t = modExp(a, q, p)
        var r = modExp(a, (q + BigInt.ONE) / BigInt.TWO, p)

        while (!t.isOne()) {
            var i = BigInt.ONE
            var t2 = modMul(t, t, p)

            while (!t2.isOne() && i < m) {
                t2 = modMul(t2, t2, p)
                i = i + BigInt.ONE
            }

            val exp = BigInt.ONE shl ((m - i - BigInt.ONE).toInt())
            val b = modExp(c, exp, p)
            m = i
            c = modMul(b, b, p)
            t = modMul(t, c, p)
            r = modMul(r, b, p)
        }

        return r
    }

    // Compute Jacobi symbol (a/n)
    fun computeJacobi(aInput: BigInt, nInput: BigInt): BigInt {
        require(nInput > BigInt.ZERO && nInput.isOdd()) {
            "n must be odd and positive"
        }

        var a = aInput % nInput
        var n = BigInt(nInput)
        var result = BigInt.ONE

        while (!a.isZero()) {
            while (a.isEven()) {
                a = a shr 1
                // (2/n) = 1 if n ≡ 1,7 (mod 8), -1 if n ≡ 3,5 (mod 8)
                val nMod8 = n % BigInt(8)
                if (nMod8 == BigInt(3) || nMod8 == BigInt(
                        5
                    )
                ) {
                    result = -result
                }
            }

            // Swap a and n
            val temp = a
            a = n
            n = temp

            // Quadratic reciprocity
            if ((a % BigInt(4)) == BigInt(
                    3
                ) && (n % BigInt(4)) == BigInt(
                    3
                )
            ) {
                result = -result
            }

            a = a % n
        }

        return if (n == BigInt.ONE) result else BigInt.ZERO
    }
}