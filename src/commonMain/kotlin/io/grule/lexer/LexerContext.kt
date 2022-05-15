package io.grule.lexer

import io.grule.matcher.CharStream

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

    fun emitEOF() {
        emit(Lexer.EOF)
    }
}
