package org.lagrange.library.crypto.ecdh

import org.lagrange.library.multiprecision.BigInt

/**
 * Elliptic curve point in affine coordinates
 */
data class EllipticPoint(
    val x: BigInt,
    val y: BigInt
) {
    // Default constructor - creates point at infinity (0,0)
    constructor() : this(BigInt.ZERO, BigInt.ZERO)

    // Constructor with Long coordinates for convenience
    constructor(xCoord: Long, yCoord: Long) : this(BigInt(xCoord), BigInt(yCoord))

    /**
     * Check if point is at infinity (identity element)
     */
    fun isDefault(): Boolean = x.isZero() && y.isZero()

    /**
     * Check if point is the identity/infinity point
     */
    fun isInfinity(): Boolean = isDefault()

    /**
     * Negate point (reflection across x-axis)
     */
    operator fun unaryMinus(): EllipticPoint = EllipticPoint(-x, -y)

    /**
     * String representation for debugging
     */
    override fun toString(): String {
        return if (isInfinity()) {
            "Point(Infinity)"
        } else {
            "Point(x=${x.toHex()}, y=${y.toHex()})"
        }
    }

    companion object {
        /**
         * Point at infinity (identity element for elliptic curve group)
         */
        val INFINITY = EllipticPoint()
    }
}