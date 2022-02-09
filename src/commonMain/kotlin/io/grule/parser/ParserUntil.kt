package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserUntil(val parser: Parser, val terminal: Parser) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var result = 0
        val node = AstNode(this)
        try {
            result += terminal.parse(tokenStream, offset + result, node)
        } catch (e: Throwable) {
            result += parser.parse(tokenStream, offset + result, node)
            result += parse(tokenStream, offset + result, node)
        }
        parentNode.merge(node)
        return result
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser || this.parser.contains(parser) || this.terminal.contains(parser)
    }
}