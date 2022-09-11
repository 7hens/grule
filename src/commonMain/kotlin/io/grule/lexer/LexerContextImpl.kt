package io.grule.lexer

import io.grule.token.CharStream
import io.grule.token.Token
import io.grule.token.TokenStream

internal class LexerContextImpl(
    private val charStream: CharStream,
    private val tokenStream: TokenStream,
) : LexerContext, CharStream by charStream {

    override fun emit(token: Token) {
        tokenStream.emit(token)
    }
}