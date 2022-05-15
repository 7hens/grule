package io.grule.parser

import io.grule.lexer.TokenStream

internal object ParserShadow : Parser() {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return 0
    }

    override fun plus(parser: Parser): Parser {
        return ParserPlus(listOf(parser))
    }

    override fun or(parser: Parser): Parser {
        return ParserOr(listOf(parser))
    }
}