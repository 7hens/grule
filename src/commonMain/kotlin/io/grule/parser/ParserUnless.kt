package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserUnless(val parser: Parser, val terminal: Parser) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        var result = 0
        val node = AstNode(parentNode.key)
        try {
            result += parser.parse(tokenStream, offset + result, node)
            result += parse(tokenStream, offset + result, node)
        } catch (e: ParserException) {
            result += terminal.parse(tokenStream, offset + result, node)
        }
        parentNode.merge(node)
        return result
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || this.parser.isRecursive(parser) || this.terminal.isRecursive(parser)
    }
}