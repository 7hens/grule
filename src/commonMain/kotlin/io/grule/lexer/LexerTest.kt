package io.grule.lexer

internal class LexerTest(private val lexer: Lexer) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        lexer.match(charStream, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$lexer)"
    }
}
