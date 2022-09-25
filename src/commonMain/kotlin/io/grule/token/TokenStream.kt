package io.grule.token

interface TokenStream {

    fun peek(offset: Int = 0): Token

    fun all(): List<Token>

    fun moveNext(count: Int)

    fun emit(token: Token)
}