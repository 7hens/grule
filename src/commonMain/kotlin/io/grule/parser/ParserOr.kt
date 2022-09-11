package io.grule.parser

import io.grule.token.TokenStream
import io.grule.node.AstNode

internal class ParserOr(val parsers: List<Parser>) : Parser {
    init {
        require(parsers.isNotEmpty())
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var error: Throwable? = null
        for (parser in parsers) {
            try {
                val node = AstNode.of(parentNode)
                val result = parser.parse(tokenStream, node, offset)
                parentNode.merge(node)
                return result
            } catch (e: ParserException) {
                error = e
            }
        }
        throw error ?: ParserException()
    }

    override infix fun or(parser: Parser): Parser {
        return ParserOr(parsers + parser)
    }

    override fun toString(): String {
        return parsers.joinToString(" | ")
    }
}