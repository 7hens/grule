package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserNot(val parser: Parser) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode(parentNode.key)
        try {
            parser.parse(tokenStream, node, offset)
        } catch (_: ParserException) {
            return 0
        }
        throw ParserException()
    }

    override fun not(): Parser {
        return parser
    }

    override fun toString(): String {
        return "(! $parser)"
    }
}