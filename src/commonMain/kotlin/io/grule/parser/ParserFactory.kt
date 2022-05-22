package io.grule.parser

import kotlin.properties.ReadOnlyProperty

class ParserFactory internal constructor() {
    operator fun invoke(fn: Parser.Companion.(Parser) -> Parser): ReadOnlyProperty<Any?, Parser> {
        return ParserProperty { ParserRecurse { fn(Parser.Companion, it) }.degrade() }
    }
}