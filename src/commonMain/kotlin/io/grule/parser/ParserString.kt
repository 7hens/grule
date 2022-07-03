package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal class ParserString(val text: String) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val token = tokenStream.peek(offset)
        if (token.text == text) {
            parentNode.add(AstNode.Terminal(text, token))
            return 1
        }
        throw ParserException("Unmatched '$text' in ${parentNode.key}, actual is $token")
    }

    override fun toString(): String {
        return text
    }
}