package io.grule.scanner

import io.grule.lexer.CharStream

internal class ScannerContextImpl(
    private val charStream: CharStream,
    private val tokenStream: TokenStream,
) : ScannerContext, CharStream by charStream {

    override fun emit(token: Token) {
        tokenStream.emit(token)
    }
}