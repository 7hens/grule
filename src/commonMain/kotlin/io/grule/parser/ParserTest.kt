package io.grule.parser

import io.grule.node.AstNode
import io.grule.token.TokenStream

internal class ParserTest(private val parser: Parser) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode.of(parentNode)
        parser.parse(tokenStream, node, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$parser)"
    }
}
