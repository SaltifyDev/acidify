package org.ntqqrev.acidify.pb

abstract class PbSchema

operator fun <S : PbSchema> S.invoke(block: S.(PbObject<S>) -> Unit) = PbObject(this, block)