package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserTest(private val parser: Parser) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        parser.parse(tokenStream, offset, node)
        return 0
    }

    override fun toString(): String {
        return "(?$parser)"
    }
}
