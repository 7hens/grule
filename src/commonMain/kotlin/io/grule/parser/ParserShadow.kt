package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal open class ParserShadow : Parser {
    private val error = UnsupportedOperationException("shadow parser")
    
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        throw error
    }

    override fun plus(parser: Parser): Parser {
        return parser
    }

    override fun or(parser: Parser): Parser {
        return parser
    }

    override fun toString(): String {
        return "<X>"
    }
}