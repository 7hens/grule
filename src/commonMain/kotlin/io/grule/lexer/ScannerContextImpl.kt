package io.grule.lexer

internal class ScannerContextImpl(
    private val charStream: CharStream,
    private val tokenStream: TokenStream,
) : ScannerContext, CharStream by charStream {

    override fun emit(token: Token) {
        tokenStream.emit(token)
    }
}