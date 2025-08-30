package org.ntqqrev.acidify.internal.service

import org.ntqqrev.acidify.internal.LagrangeClient

internal interface Service<T, R> {
    val cmd: String
    fun build(client: LagrangeClient, payload: T): ByteArray
    fun parse(client: LagrangeClient, payload: ByteArray): R
}

internal interface NoInputService<R> : Service<Unit, R>

internal abstract class NoOutputService<T> : Service<T, Unit> {
    override fun parse(client: LagrangeClient, payload: ByteArray) = Unit
}