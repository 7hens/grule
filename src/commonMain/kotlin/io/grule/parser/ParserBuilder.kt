package io.grule.parser

import io.grule.lexer.TokenChannel

internal open class ParserBuilder : Parser() {
    private var myParser: Parser = Shadow

    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        myParser.name = name
        return myParser.parse(channel, offset, parentNode)
    }

    override fun plus(parser: Parser): Parser {
        myParser = myParser.plus(parser)
        return this
    }

    override fun or(parser: Parser): Parser {
        myParser = myParser.or(parser)
        return this
    }

    object Shadow : Parser() {
        override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
            return 0
        }

        override fun plus(parser: Parser): Parser {
            return ParserPlus(mutableListOf(parser))
        }

        override fun or(parser: Parser): Parser {
            return ParserOr(mutableListOf(parser))
        }
    }
}