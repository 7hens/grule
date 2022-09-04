package io.grule.lexer

interface TokenStream {

    fun peek(offset: Int): Token

    fun all(): List<Token>

    fun moveNext(count: Int)

    fun emit(token: Token)
}