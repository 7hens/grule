package io.grule.lexer

import io.grule.matcher.CharStream

internal class LexerContextImpl(
    private val charStream: CharStream,
    private val tokenStream: TokenStream,
) : LexerContext, CharStream by charStream {

    override fun emit(token: Token) {
        tokenStream.emit(token)
    }
}