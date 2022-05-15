package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserPlus(private val parsers: List<Parser>) : Parser() {
    init {
        require(parsers.isNotEmpty())
    }

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
        return ParserPlus(parsers + parser)
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || (parsers.firstOrNull()?.isRecursive(parser) ?: false)
    }
}