package io.grule.lexer

internal class LexerTest(private val lexer: Lexer) : Lexer {
    override fun match(context: LexerContext, offset: Int): Int {
        lexer.match(context, offset)
        return 0
    }

    override fun toString(): String {
        return "(?$lexer)"
    }
}
