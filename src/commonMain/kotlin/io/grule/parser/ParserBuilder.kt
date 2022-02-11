package io.grule.parser

import io.grule.lexer.TokenStream
import kotlin.reflect.KProperty

internal open class ParserBuilder : Parser() {
    var myParser: Parser = Shadow

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val node = AstNode(this)
        val result = myParser.parse(tokenStream, offset, node)
        if (name != null) {
            parentNode.add(node)
        } else {
            parentNode.merge(node)
        }
        return result
    }

    override fun toString(): String {
        return name ?: myParser.toString()
    }

    override fun plus(parser: Parser): Parser {
        myParser = myParser.plus(parser)
        return this
    }

    override fun or(parser: Parser): Parser {
        myParser = myParser.or(parser)
        return this
    }
    
    override fun contains(parser: Parser): Boolean {
        return this === parser || this.myParser.contains(parser)
    }

    object Shadow : Parser() {
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
}