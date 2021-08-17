package io.grule.parser

import io.grule.lexer.TokenChannel

internal class ParserPlus(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(this)
        var result = 0
        for (parser in parsers) {
            result += parser.tryParse(channel, offset + result, node)
        }
        parentNode.merge(node)
        return result
    }

    override operator fun plus(parser: Parser): Parser {
        parsers.add(parser)
        return this
    }
}