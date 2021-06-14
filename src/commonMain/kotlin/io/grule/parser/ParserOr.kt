package io.grule.parser

import io.grule.CompositeException
import io.grule.lexer.TokenChannel

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        val compositeException = CompositeException()
        for (parser in parsers) {
            try {
                return parser.tryParse(channel, offset, parentNode)
            } catch (e: Throwable) {
                compositeException.add(e)
            }
        }
        throw compositeException.get()
    }

    override infix fun or(parser: Parser): Parser {
        parsers.add(parser)
        return this
    }
}