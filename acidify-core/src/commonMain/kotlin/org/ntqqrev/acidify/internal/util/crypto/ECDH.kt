package org.ntqqrev.acidify.internal.util.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import org.ntqqrev.acidify.internal.util.md5

internal class ECDH(
    private val curve: EllipticCurve
) {
    private val secret = createSecret()
    private val public = createPublic(secret)

    fun getPublicKey(compress: Boolean): ByteArray {
        return packPublic(public, compress)
    }

    fun keyExchange(publicKey: ByteArray, isHash: Boolean = true): ByteArray {
        val ecPub = unpackPublic(publicKey)
        val ecShared = createShared(secret, ecPub)
        return packShared(ecShared, isHash)
    }

    private fun unpackPublic(publicKey: ByteArray): EllipticPoint {
        val length = publicKey.size
        if (length != curve.size * 2 + 1 && length != curve.size + 1) throw Exception("Length does not match.")

        val x = ByteArray(curve.size + 1)
        arraycopy(publicKey, 1, x, 1, curve.size)

        if (publicKey[0] == 0x04.toByte()) {
            val y = ByteArray(curve.size + 1)
            arraycopy(publicKey, curve.size + 1, y, 1, curve.size)

            return EllipticPoint(x.toBigInteger(), y.toBigInteger())
        }

        val px = x.toBigInteger()
        val x3 = px * px * px
        val ax = px * curve.a
        val right = (x3 + ax + curve.b) % curve.p

        val tmp = (curve.p + 1.toBigInteger()) shr 2
        var py = right.modPow(tmp, curve.p)

        if (!(py.isEven() xor (publicKey[0] == 0x02.toByte()) || !py.isEven() xor (publicKey[0] == 0x03.toByte()))) {
            py = curve.p - py
        }

        return EllipticPoint(px, py)
    }

    private fun packPublic(ecPub: EllipticPoint, compress: Boolean = true): ByteArray {
        if (compress) {
            var result = ecPub.x.toByteArray()
            if (result.size == curve.size) result = byteArrayOf(0x0) + result
            result[0] = if (ecPub.y.isEven() xor (ecPub.y.signum() < 0)) 0x02 else 0x03
            return result
        }

        var x = ecPub.x.toByteArray()
        var y = ecPub.y.toByteArray()

        if (x.size != curve.size) {
            val result = ByteArray(curve.size)
            arraycopy(x, 1, result, 0, curve.size)
            x = result
        }
        if (y.size != curve.size) {
            val result = ByteArray(curve.size)
            arraycopy(y, 1, result, 0, curve.size)
            y = result
        }

        return byteArrayOf(0x04) + x + y
    }


    private fun packShared(ecShared: EllipticPoint, isHash: Boolean): ByteArray {
        var x = ecShared.x.toByteArray()
        if (x.size != curve.size) {
            val result = ByteArray(curve.size)
            arraycopy(x, 1, result, 0, curve.size)
            x = result
        }
        if (!isHash) return x

        return x.md5()
    }

    private fun createPublic(ecSec: BigInteger): EllipticPoint {
        return createShared(ecSec, curve.g)
    }

    private fun createSecret(): BigInteger {
        var result: BigInteger
        val array = ByteArray(curve.size + 1)

        do {
            for (i in 0 until curve.size) {
                array[i] = (0..255).random().toByte()
            }
            array[curve.size] = 0
            result = array.toBigInteger()
        } while (result < 1.toBigInteger() || result >= curve.n)

        return result
    }

    private fun createShared(ecSec: BigInteger, ecPub: EllipticPoint): EllipticPoint {
        if (ecSec % curve.n == 0.toBigInteger() || ecPub.isDefault()) {
            return EllipticPoint(0.toBigInteger(), 0.toBigInteger())
        }
        if (ecSec < 0.toBigInteger()) {
            return createShared(-ecSec, -ecPub)
        }

        if (!curve.checkOn(ecPub)) {
            throw Exception("Public key does not correct.")
        }

        var pr = EllipticPoint(0.toBigInteger(), 0.toBigInteger())
        var pa = ecPub
        var sec = ecSec
        while (sec > 0.toBigInteger()) {
            if (sec and 1.toBigInteger() > 0.toBigInteger()) {
                pr = pointAdd(pr, pa)
            }

            pa = pointAdd(pa, pa)
            sec = sec shr 1
        }

        if (!curve.checkOn(pr)) throw Exception("Unknown error.")

        return pr
    }

    private fun pointAdd(p1: EllipticPoint, p2: EllipticPoint): EllipticPoint {
        if (p1.isDefault()) return p2
        if (p2.isDefault()) return p1
        if (!curve.checkOn(p1) || !curve.checkOn(p2)) throw Exception()

        val x1 = p1.x
        val x2 = p2.x
        val y1 = p1.y
        val y2 = p2.y
        val m = if (x1 == x2) {
            if (y1 == y2) (3.toBigInteger() * x1 * x1 + curve.a) * modInverse(y1 shl 1, curve.p)
            else return EllipticPoint(0.toBigInteger(), 0.toBigInteger())
        } else {
            (y1 - y2) * modInverse(x1 - x2, curve.p)
        }

        val xr = mod(m * m - x1 - x2, curve.p)
        val yr = mod(m * (x1 - xr) - y1, curve.p)
        val pr = EllipticPoint(xr, yr)

        if (!curve.checkOn(pr)) throw Exception()
        return pr
    }

    private fun modInverse(a: BigInteger, p: BigInteger): BigInteger {
        if (a < 0.toBigInteger()) return p - modInverse(-a, p)

        val g = a.gcd(p)
        if (g != 1.toBigInteger()) throw Exception("Inverse does not exist.")

        return a.modPow(p - 2.toBigInteger(), p)
    }

    private fun mod(a: BigInteger, b: BigInteger): BigInteger {
        var result = a % b
        if (result < 0.toBigInteger()) result += b
        return result
    }

    companion object {
        val secp192k1 = ECDH(EllipticCurve.secp192k1)
        val prime256v1 = ECDH(EllipticCurve.prime256v1)
    }
}

internal class EllipticCurve(
    val p: BigInteger,
    val a: BigInteger,
    val b: BigInteger,
    val g: EllipticPoint,
    val n: BigInteger,
    val h: Int,
    val size: Int,
    val packSize: Int
) {
    fun checkOn(point: EllipticPoint): Boolean {
        return (point.y * point.y - point.x * point.x * point.x - a * point.x - b) % p == BigInteger.ZERO
    }

    companion object {
        val secp192k1 = EllipticCurve(
            p = "0FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37".toBigInteger(16),
            a = BigInteger.ZERO,
            b = BigInteger(3),
            g = EllipticPoint(
                x = "0DB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D".toBigInteger(16),
                y = "09B2F2F6D9C5628A7844163D015BE86344082AA88D95E2F9D".toBigInteger(16)
            ),
            n = "0FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D".toBigInteger(16),
            h = 1,
            size = 24,
            packSize = 24
        )

        val prime256v1 = EllipticCurve(
            p = "0FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF".toBigInteger(16),
            a = "0FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC".toBigInteger(16),
            b = "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B".toBigInteger(16),
            g = EllipticPoint(
                x = "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296".toBigInteger(16),
                y = "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5".toBigInteger(16)
            ),
            n = "0FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551".toBigInteger(16),
            h = 1,
            size = 32,
            packSize = 16
        )
    }
}

internal data class EllipticPoint(
    val x: BigInteger,
    val y: BigInteger
) {
    fun isDefault(): Boolean {
        return x == BigInteger.ZERO && y == BigInteger.ZERO
    }

    operator fun unaryMinus(): EllipticPoint {
        return EllipticPoint(-x, -y)
    }
}

internal fun BigInteger.isEven(): Boolean = this.and(BigInteger.ONE) == BigInteger.ZERO

private fun ByteArray.toBigInteger(): BigInteger = BigInteger.fromByteArray(this, Sign.POSITIVE)

private fun arraycopy(src: ByteArray, srcPos: Int, dest: ByteArray, destPos: Int, length: Int) {
    for (i in 0 until length) {
        dest[destPos + i] = src[srcPos + i]
    }
}

private fun BigInteger.modPow(exponent: BigInteger, m: BigInteger): BigInteger {
    var result = 1.toBigInteger()
    var base = this % m
    var exp = exponent

    while (exp > 0.toBigInteger()) {
        if (exp and 1.toBigInteger() == 1.toBigInteger()) {
            result = (result * base) % m
        }
        exp = exp shr 1
        base = (base * base) % m
    }

    return result
}
