package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserOr(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var error: Throwable? = null
        for (parser in parsers) {
            try {
                val node = AstNode(parentNode.key)
                val result = parser.parse(tokenStream, offset, node)
                parentNode.merge(node)
                return result
            } catch (e: ParserException) {
                error = e
            }
        }
        throw error ?: ParserException()
    }

    override infix fun or(parser: Parser): Parser {
        parsers.add(parser)
        return this
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser || parsers.any { it.contains(parser) }
    }
}