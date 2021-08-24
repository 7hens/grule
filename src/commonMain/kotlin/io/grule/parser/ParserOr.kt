package io.grule.parser

import io.grule.CompositeException
import io.grule.lexer.TokenStream

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val compositeException = CompositeException()
        for (parser in parsers) {
            try {
                return parser.tryParse(tokenStream, offset, parentNode)
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