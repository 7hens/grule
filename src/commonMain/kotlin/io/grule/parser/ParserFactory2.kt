package io.grule.parser

import kotlin.properties.ReadOnlyProperty

class ParserFactory2 {
    operator fun invoke(fn: ParserMatcherDsl.() -> ParserMatcher): ReadOnlyProperty<Any?, Parser> {
        return ParserProperty { fn(ParserMatcherDsl) }
    }
}