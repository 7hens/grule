package io.grule.lexer

interface TokenStream {
    val charStream: CharStream

    fun peek(offset: Int): Token

    fun moveNext(count: Int)

    fun emit(token: Token)

    fun emit(scanner: Scanner, text: String)

    fun emitEOF() {
        emit(Scanners.EOF, "<EOF>")
    }
}