package io.grule.parser

import io.grule.lexer.Lexer
import kotlin.properties.ReadOnlyProperty

class ParserFactory internal constructor(val lexer: Lexer) {

    operator fun invoke(fn: ParserSupplier): ReadOnlyProperty<Any?, Parser> {
        return ParserProperty(lexer) { fn(ParserDsl).cache() }
    }

}