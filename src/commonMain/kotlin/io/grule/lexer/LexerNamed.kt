package io.grule.lexer

internal class LexerNamed(private val lexer: Lexer, private val name: String) : Lexer {
    override fun lex(context: LexerContext) {
        return lexer.lex(context)
    }

    override fun toString(): String {
        return name
    }
}