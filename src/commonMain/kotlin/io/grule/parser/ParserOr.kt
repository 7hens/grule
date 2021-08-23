package io.grule.parser

import io.grule.CompositeException
import io.grule.lexer.TokenStream

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(channel: TokenStream, offset: Int, parentNode: AstNode): Int {
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