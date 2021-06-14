package io.grule.parser

import io.grule.lexer.TokenChannel

internal class ParserRepeatWith(val parser: Parser, val separator: Parser) : Parser() {
    override val isFlatten = true

    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(this)
        var result = parser.tryParse(channel, offset, node)
        while (true) {
            try {
                result += separator.tryParse(channel, offset + result, node)
                result += parser.tryParse(channel, offset + result, node)
            } catch (_: Throwable) {
                parentNode.merge(node)
                return result
            }
        }
    }
}