package io.grule.lexer

import io.grule.token.CharStream
import io.grule.token.Token

interface LexerContext : CharStream {
    fun emit(token: Token)

    fun emit(lexer: Lexer, text: String) {
        emit(Token(lexer, text, position))
    }

    fun emit(lexer: Lexer, count: Int) {
        emit(lexer, getText(0, count))
    }

    fun emit(lexer: Lexer) {
        emit(lexer, "<$lexer>")
    }

    @Deprecated("Use emitEof instead", ReplaceWith("emitEof()"))
    fun emitEOF() {
        emitEof()
    }

    fun emitEof() {
        emit(Lexer.EOF)
    }
}
