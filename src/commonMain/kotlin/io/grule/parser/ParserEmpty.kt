package io.grule.parser

import io.grule.token.TokenStream
import io.grule.node.AstNode

internal object ParserEmpty : ParserShadow() {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return 0
    }
}