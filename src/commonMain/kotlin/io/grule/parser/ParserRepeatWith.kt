package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserRepeatWith(val parser: Parser, val separator: Parser) : Parser() {
    override val isFlatten = true

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(this)
        var result = parser.tryParse(tokenStream, offset, node)
        while (true) {
            try {
                result += separator.tryParse(tokenStream, offset + result, node)
                result += parser.tryParse(tokenStream, offset + result, node)
            } catch (_: Throwable) {
                parentNode.merge(node)
                return result
            }
        }
    }
}