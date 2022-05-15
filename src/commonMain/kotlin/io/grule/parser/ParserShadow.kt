package io.grule.parser

import io.grule.lexer.TokenStream

internal object ParserShadow : Parser() {
    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        return 0
    }

    override fun plus(parser: Parser): Parser {
        return ParserPlus(mutableListOf(parser))
    }

    override fun or(parser: Parser): Parser {
        return ParserOr(mutableListOf(parser))
    }
}