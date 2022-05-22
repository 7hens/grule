package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserTest(private val parser: Parser) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode(parentNode.key)
        parser.parse(tokenStream, node, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$parser)"
    }
}
