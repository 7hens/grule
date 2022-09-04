package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

open class ParserBuilder : Parser {
    internal var delegate: Parser = ParserShadow()

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return delegate.parse(tokenStream, parentNode, offset)
    }

    override fun toString(): String {
        return delegate.toString()
    }

    override fun plus(parser: Parser): Parser {
        delegate = delegate.plus(parser)
        return this
    }

    override fun or(parser: Parser): Parser {
        delegate = delegate.or(parser)
        return this
    }

}