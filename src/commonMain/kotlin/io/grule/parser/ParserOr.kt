package io.grule.parser

import io.grule.CompositeException
import io.grule.lexer.TokenStream

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val compositeException = CompositeException()
        val node = AstNode(this)
        for (parser in parsers) {
            try {
                val result = parser.tryParse(tokenStream, offset, node)
                parentNode.merge(node)
                return result
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