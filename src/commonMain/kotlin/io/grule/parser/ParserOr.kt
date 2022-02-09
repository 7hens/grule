package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        var error: Throwable = ParserException()
        for (parser in parsers) {
            try {
                val result = parser.parse(tokenStream, offset, node)
                parentNode.merge(node)
                return result
            } catch (e: Throwable) {
                error = e
            }
        }
        throw error
    }

    override infix fun or(parser: Parser): Parser {
        parsers.add(parser)
        return this
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser || parsers.any { it.contains(parser) }
    }
}