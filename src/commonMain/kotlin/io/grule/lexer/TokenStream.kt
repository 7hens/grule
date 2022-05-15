package io.grule.lexer

interface TokenStream {
    fun peek(offset: Int): Token

    fun moveNext(count: Int)

    fun emit(token: Token)

    fun emit(scanner: Scanner, text: String)
    
    fun emit(scanner: Scanner) {
        emit(scanner, "$scanner")
    }

    fun emitEOF() {
        emit(Scanners.EOF)
    }
}