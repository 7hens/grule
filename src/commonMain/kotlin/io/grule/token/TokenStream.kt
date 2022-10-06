package io.grule.token

interface TokenStream {

    fun peek(offset: Int = 0): Token

    fun all(): Sequence<Token>

    fun moveNext(count: Int)

    fun emit(token: Token)
}