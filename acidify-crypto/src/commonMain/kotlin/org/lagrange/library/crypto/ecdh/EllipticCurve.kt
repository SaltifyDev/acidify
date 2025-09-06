package org.lagrange.library.crypto.ecdh

import org.lagrange.library.multiprecision.BigInt
import org.lagrange.library.multiprecision.ModularOps

/**
 * Elliptic curve parameters for y^2 = x^3 + ax + b (mod p)
 */
data class EllipticCurve(
    val p: BigInt,           // Prime modulus
    val a: BigInt,           // Curve parameter a
    val b: BigInt,           // Curve parameter b
    val g: EllipticPoint,    // Base point (generator)
    val n: BigInt,           // Order of base point
    val h: BigInt,           // Cofactor
    val size: Int,           // Key size in bytes
    val packSize: Int        // Packed size for serialization
) {
    /**
     * Check if a point lies on the curve: y^2 ≡ x^3 + ax + b (mod p)
     */
    fun checkOn(point: EllipticPoint): Boolean {
        if (point.isInfinity()) {
            return true  // Point at infinity is always on the curve
        }

        // Compute y^2 mod p
        val ySquared = ModularOps.modMul(point.y, point.y, p)

        // Compute x^3 + ax + b mod p
        val xSquared = ModularOps.modMul(point.x, point.x, p)
        val xCubed = ModularOps.modMul(xSquared, point.x, p)
        val ax = ModularOps.modMul(a, point.x, p)
        var rightSide = ModularOps.modAdd(xCubed, ax, p)
        rightSide = ModularOps.modAdd(rightSide, b, p)

        return ySquared == rightSide
    }

    companion object {
        /**
         * secp192k1 curve parameters
         * p = 2^192 - 2^32 - 2^12 - 2^8 - 2^7 - 2^6 - 2^3 - 1
         * p = 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37
         */
        fun secp192k1(): EllipticCurve {
            // p
            val pBytes = byteArrayOf(
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFE.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xEE.toByte(), 0x37.toByte()
            )

            // Generator point coordinates
            // gx = 0xDB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D
            // gy = 0x9B2F2F6D9C5628A7844163D015BE86344082AA88D95E2F9D
            val gxBytes = byteArrayOf(
                0xDB.toByte(), 0x4F.toByte(), 0xF1.toByte(), 0x0E.toByte(),
                0xC0.toByte(), 0x57.toByte(), 0xE9.toByte(), 0xAE.toByte(),
                0x26.toByte(), 0xB0.toByte(), 0x7D.toByte(), 0x02.toByte(),
                0x80.toByte(), 0xB7.toByte(), 0xF4.toByte(), 0x34.toByte(),
                0x1D.toByte(), 0xA5.toByte(), 0xD1.toByte(), 0xB1.toByte(),
                0xEA.toByte(), 0xE0.toByte(), 0x6C.toByte(), 0x7D.toByte()
            )
            val gyBytes = byteArrayOf(
                0x9B.toByte(), 0x2F.toByte(), 0x2F.toByte(), 0x6D.toByte(),
                0x9C.toByte(), 0x56.toByte(), 0x28.toByte(), 0xA7.toByte(),
                0x84.toByte(), 0x41.toByte(), 0x63.toByte(), 0xD0.toByte(),
                0x15.toByte(), 0xBE.toByte(), 0x86.toByte(), 0x34.toByte(),
                0x40.toByte(), 0x82.toByte(), 0xAA.toByte(), 0x88.toByte(),
                0xD9.toByte(), 0x5E.toByte(), 0x2F.toByte(), 0x9D.toByte()
            )

            // Order of the generator point
            // n = 0xFFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D
            val nBytes = byteArrayOf(
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFE.toByte(),
                0x26.toByte(), 0xF2.toByte(), 0xFC.toByte(), 0x17.toByte(),
                0x0F.toByte(), 0x69.toByte(), 0x46.toByte(), 0x6A.toByte(),
                0x74.toByte(), 0xDE.toByte(), 0xFD.toByte(), 0x8D.toByte()
            )

            return EllipticCurve(
                p = BigInt.fromBytes(pBytes, true),
                a = BigInt.ZERO,
                b = BigInt(3),
                g = EllipticPoint(
                    BigInt.fromBytes(gxBytes, true),
                    BigInt.fromBytes(gyBytes, true)
                ),
                n = BigInt.fromBytes(nBytes, true),
                h = BigInt.ONE,
                size = 24,
                packSize = 24
            )
        }

        /**
         * P-256 (secp256r1 / prime256v1) curve parameters
         * p = 2^256 - 2^224 + 2^192 + 2^96 - 1
         * p = 0xFFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF
         */
        fun prime256v1(): EllipticCurve {
            // p
            val pBytes = byteArrayOf(
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
            )

            // a = p - 3
            // a = 0xFFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC
            val aBytes = byteArrayOf(
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFC.toByte()
            )

            // b = 0x5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B
            val bBytes = byteArrayOf(
                0x5A.toByte(), 0xC6.toByte(), 0x35.toByte(), 0xD8.toByte(),
                0xAA.toByte(), 0x3A.toByte(), 0x93.toByte(), 0xE7.toByte(),
                0xB3.toByte(), 0xEB.toByte(), 0xBD.toByte(), 0x55.toByte(),
                0x76.toByte(), 0x98.toByte(), 0x86.toByte(), 0xBC.toByte(),
                0x65.toByte(), 0x1D.toByte(), 0x06.toByte(), 0xB0.toByte(),
                0xCC.toByte(), 0x53.toByte(), 0xB0.toByte(), 0xF6.toByte(),
                0x3B.toByte(), 0xCE.toByte(), 0x3C.toByte(), 0x3E.toByte(),
                0x27.toByte(), 0xD2.toByte(), 0x60.toByte(), 0x4B.toByte()
            )

            // Generator point coordinates
            // gx = 0x6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296
            // gy = 0x4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5
            val gxBytes = byteArrayOf(
                0x6B.toByte(), 0x17.toByte(), 0xD1.toByte(), 0xF2.toByte(),
                0xE1.toByte(), 0x2C.toByte(), 0x42.toByte(), 0x47.toByte(),
                0xF8.toByte(), 0xBC.toByte(), 0xE6.toByte(), 0xE5.toByte(),
                0x63.toByte(), 0xA4.toByte(), 0x40.toByte(), 0xF2.toByte(),
                0x77.toByte(), 0x03.toByte(), 0x7D.toByte(), 0x81.toByte(),
                0x2D.toByte(), 0xEB.toByte(), 0x33.toByte(), 0xA0.toByte(),
                0xF4.toByte(), 0xA1.toByte(), 0x39.toByte(), 0x45.toByte(),
                0xD8.toByte(), 0x98.toByte(), 0xC2.toByte(), 0x96.toByte()
            )
            val gyBytes = byteArrayOf(
                0x4F.toByte(), 0xE3.toByte(), 0x42.toByte(), 0xE2.toByte(),
                0xFE.toByte(), 0x1A.toByte(), 0x7F.toByte(), 0x9B.toByte(),
                0x8E.toByte(), 0xE7.toByte(), 0xEB.toByte(), 0x4A.toByte(),
                0x7C.toByte(), 0x0F.toByte(), 0x9E.toByte(), 0x16.toByte(),
                0x2B.toByte(), 0xCE.toByte(), 0x33.toByte(), 0x57.toByte(),
                0x6B.toByte(), 0x31.toByte(), 0x5E.toByte(), 0xCE.toByte(),
                0xCB.toByte(), 0xB6.toByte(), 0x40.toByte(), 0x68.toByte(),
                0x37.toByte(), 0xBF.toByte(), 0x51.toByte(), 0xF5.toByte()
            )

            // Order of the generator point
            // n = 0xFFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551
            val nBytes = byteArrayOf(
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                0xBC.toByte(), 0xE6.toByte(), 0xFA.toByte(), 0xAD.toByte(),
                0xA7.toByte(), 0x17.toByte(), 0x9E.toByte(), 0x84.toByte(),
                0xF3.toByte(), 0xB9.toByte(), 0xCA.toByte(), 0xC2.toByte(),
                0xFC.toByte(), 0x63.toByte(), 0x25.toByte(), 0x51.toByte()
            )

            return EllipticCurve(
                p = BigInt.fromBytes(pBytes, true),
                a = BigInt.fromBytes(aBytes, true),
                b = BigInt.fromBytes(bBytes, true),
                g = EllipticPoint(
                    BigInt.fromBytes(gxBytes, true),
                    BigInt.fromBytes(gyBytes, true)
                ),
                n = BigInt.fromBytes(nBytes, true),
                h = BigInt.ONE,
                size = 32,
                packSize = 16
            )
        }
    }
}