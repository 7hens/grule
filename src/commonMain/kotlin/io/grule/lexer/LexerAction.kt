package io.grule.lexer

internal class LexerAction(val lexer: Lexer, val fn: (TokenChannel, Int) -> Unit) : Lexer() {
    override fun match(input: CharStream, offset: Int): Int {
        return lexer.match(input, offset)
    }

    override fun onMatch(channel: TokenChannel, matchNum: Int) {
        fn(channel, matchNum)
    }
}