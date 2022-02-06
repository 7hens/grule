package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserNot(val parser: Parser) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        try {
            parser.parse(tokenStream, offset, node)
        } catch (_: Throwable) {
            return 0
        }
        throw ParserException()
    }

    override fun not(): Parser {
        return parser
    }
}