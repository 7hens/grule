package io.grule.matcher2.parser

import kotlin.properties.ReadOnlyProperty

class ParserFactory2 {
    operator fun invoke(fn: ParserMatcherDsl.() -> ParserMatcher): ReadOnlyProperty<Any?, Parser> {
        return ParserProperty { fn(ParserMatcherDsl) }
    }
}