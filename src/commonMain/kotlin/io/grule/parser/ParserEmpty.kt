package io.grule.parser

import io.grule.lexer.TokenStream

internal object ParserEmpty : ParserShadow() {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return 0
    }
}