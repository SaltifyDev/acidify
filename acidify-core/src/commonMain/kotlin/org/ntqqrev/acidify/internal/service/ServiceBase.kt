package org.ntqqrev.acidify.internal.service

import org.ntqqrev.acidify.internal.LagrangeClient

internal abstract class Service<T, R>(val cmd: String) {
    abstract fun build(client: LagrangeClient, payload: T): ByteArray
    abstract fun parse(client: LagrangeClient, payload: ByteArray): R
}

internal abstract class NoInputService<R>(cmd: String) : Service<Unit, R>(cmd)

internal abstract class NoOutputService<T>(cmd: String) : Service<T, Unit>(cmd) {
    override fun parse(client: LagrangeClient, payload: ByteArray) = Unit
}