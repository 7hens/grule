package io.grule.lexer

internal class LexerAction(val lexer: Lexer, val fn: (TokenStream, Int) -> Unit) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        return lexer.match(charStream, offset)
    }

    override fun onMatch(tokenStream: TokenStream, matchNum: Int) {
        fn(tokenStream, matchNum)
    }
}