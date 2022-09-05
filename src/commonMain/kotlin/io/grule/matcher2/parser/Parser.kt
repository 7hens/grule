package io.grule.matcher2.parser

import io.grule.lexer.Lexer
import io.grule.lexer.TokenStream
import io.grule.node.AstNode
import io.grule.node.KeyProvider

interface Parser : ParserMatcher, KeyProvider {
    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = this + Lexer.EOF
        val node = AstNode.of(this)
        val status = ParserMatcherStatus(tokenStream, 0, node)
        status.apply(mainParser)
        return node.first()
    }
}