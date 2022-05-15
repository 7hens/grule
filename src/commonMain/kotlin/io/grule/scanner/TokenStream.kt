package io.grule.scanner

interface TokenStream {
    fun peek(offset: Int): Token

    fun moveNext(count: Int)

    fun emit(token: Token)
}