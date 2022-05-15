package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.TokenStream

internal class ParserLexer(private val lexer: Lexer) : Parser() {

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val token = tokenStream.peek(offset)
        if (token.lexer == lexer) {
            parentNode.add(AstNode.Terminal(lexer, token))
            return 1
        }
        throw ParserException("Unmatched '$lexer' in ${parentNode.key}, actual is $token")
    }
}