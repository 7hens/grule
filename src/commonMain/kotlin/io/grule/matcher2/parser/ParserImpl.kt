package io.grule.matcher2.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode
import io.grule.node.KeyProvider

internal class ParserImpl(val matcher: ParserMatcher, val keyProvider: KeyProvider) : Parser {

    override fun parse(tokenStream: TokenStream): AstNode {
        val node = AstNode.of(keyProvider)
        val status = ParserMatcherContext.from(tokenStream, node)
        matcher.match(status)
        return node
    }

    override fun toString(): String {
        return keyProvider.toString()
    }
}