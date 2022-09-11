package io.grule.parser

import io.grule.token.TokenStream
import io.grule.node.AstNode

internal class ParserComposable(private val delegate: Parser) : Parser, Composable {
    override val key: Any get() = delegate.key

    override fun composable(): Parser {
        return this
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return delegate.parse(tokenStream, parentNode, offset)
    }
}