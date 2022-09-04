package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal class ParserLexer(private val lexer: Lexer) : Parser {

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val token = tokenStream.peek(offset)
        if (token.lexer == lexer) {
            parentNode.add(AstNode.of(lexer, token))
            return 1
        }
        throw ParserException("Unmatched <$lexer> in ${parentNode.key}, actual is $token")
    }

    override fun toString(): String {
        return "<$lexer>"
    }
}