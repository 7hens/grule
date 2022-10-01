package io.grule.lexer

import io.grule.token.CharStream
import io.grule.token.TextRange
import io.grule.token.Token

interface LexerContext : CharStream {

    fun emit(token: Token)

    @Deprecated("Use emit(token) instead",
        ReplaceWith("emit(Token(lexer, text, textRange))",
            "io.grule.token.Token",
            "io.grule.token.TextRange"))
    fun emit(lexer: Lexer, text: String) {
        emit(Token(lexer, text, TextRange.of(position)))
    }

    fun emit(lexer: Lexer, count: Int = 0) {
        val text = getText(0, count)
        val textRange = moveNext(count)
        emit(Token(lexer, text, textRange))
    }

    @Deprecated("Use emitEof instead", ReplaceWith("emitEof()"))
    fun emitEOF() {
        emitEof()
    }

    fun emitEof() {
        emit(Lexer.EOF)
    }
}
