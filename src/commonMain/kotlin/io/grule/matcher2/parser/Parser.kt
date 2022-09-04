package io.grule.matcher2.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

interface Parser {
    fun parse(tokenStream: TokenStream): AstNode
}