package org.ntqqrev.acidify.internal.util

expect fun flashTransferPostWithBlock(url: String, body: ByteArray): ByteArray

expect fun highwayPostWithBlock(url: String, body: ByteArray): ByteArray