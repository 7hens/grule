package io.grule.parser

import kotlin.properties.ReadOnlyProperty

class ParserFactory {
    operator fun invoke(fn: ParserSupplier): ReadOnlyProperty<Any?, Parser> {
        return ParserProperty { fn(ParserDsl).cache() }
    }
}