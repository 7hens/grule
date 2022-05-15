package io.grule.parser

import io.grule.lexer.TokenStream

internal open class ParserBuilder : Parser() {
    var myParser: Parser = ParserShadow

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        return if (isNamed) {
            val node = AstNode(this)
            val result = myParser.parse(tokenStream, offset, node)
            parentNode.add(node)
            result
        } else {
            val node = AstNode(parentNode.key)
            val result = myParser.parse(tokenStream, offset, node)
            parentNode.merge(node)
            result
        }
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

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || (!isNamed && myParser.isRecursive(parser))
    }

}