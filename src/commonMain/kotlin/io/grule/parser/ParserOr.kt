package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal class ParserOr(val parsers: List<Parser>) : Parser {
    init {
        require(parsers.isNotEmpty())
    }
    
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var error: Throwable? = null
        for (parser in parsers) {
            try {
                val node = AstNode(parentNode.key)
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