package org.ntqqrev.acidify.crypto.ecdh

/**
 * Main ECDH (Elliptic Curve Diffie-Hellman) cryptographic system
 * This module provides all necessary components for ECDH key exchange
 */
object Ecdh {
    /**
     * Convenience references for commonly used curves
     */
    val Secp192K1: EllipticCurve = EllipticCurve.secp192k1()
    val Prime256V1: EllipticCurve = EllipticCurve.prime256v1()

    /**
     * Create a new ECDH key pair for the specified curve
     */
    fun generateKeyPair(curve: EllipticCurve = Prime256V1): EcdhProvider {
        return EcdhProvider(curve)
    }

    /**
     * Load an ECDH key pair from an existing secret
     */
    fun fromSecret(curve: EllipticCurve, secret: ByteArray): EcdhProvider {
        return EcdhProvider(curve, secret)
    }

    /**
     * Perform key exchange between two parties
     * @param alice Alice's ECDH provider
     * @param bobPublicKey Bob's public key bytes
     * @param hash Whether to hash the shared secret with MD5
     * @return The shared secret
     */
    fun keyExchange(
        alice: EcdhProvider,
        bobPublicKey: ByteArray,
        hash: Boolean = false
    ): ByteArray {
        return alice.keyExchange(bobPublicKey, hash)
    }
}