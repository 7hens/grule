package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserPlus(val parsers: MutableList<Parser>) : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(parentNode.key)
        var result = 0
        for (parser in parsers) {
            result += parser.parse(tokenStream, offset + result, node)
        }
        parentNode.merge(node)
        return result
    }

    override operator fun plus(parser: Parser): Parser {
        parsers.add(parser)
        return this
    }

    override fun contains(parser: Parser): Boolean {
        return this === parser || parsers.any { it.contains(parser) }
    }
}