package io.grule.parser

import kotlin.properties.ReadOnlyProperty

class ParserFactory internal constructor() {
    operator fun invoke(fn: Parser.Companion.(Parser) -> Parser): ReadOnlyProperty<Any?, Parser> {
        return object : ParserProperty() {
            override fun getParser(): Parser {
                return ParserRecurse(this) { fn(Parser.Companion, it) }.degrade()
            }
        }
    }

    operator fun invoke(): ReadOnlyProperty<Any, Parser> {
        return object : ParserProperty() {
            override fun getParser(): Parser {
                return ParserEmpty
            }
        }
    }
}