@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.RTLD_LAZY
import platform.posix.dlopen
import platform.posix.dlsym

const val soPath = "./lib/linux-x64/liblagrangecodec.so"

actual fun loadCodecLibrary(): COpaquePointer {
    val handle = dlopen(soPath, RTLD_LAZY)
    require(handle != null) { "Cannot load shared library $soPath" }
    return handle
}

actual fun loadCodecFunction(
    handle: COpaquePointer,
    symbol: String
): COpaquePointer {
    val sym = dlsym(handle, symbol)
    require(sym != null) { "Cannot load symbol $symbol" }
    return sym
}