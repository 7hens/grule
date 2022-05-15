package io.grule.lexer

object LexerShadow : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        throw UnsupportedOperationException("shadow lexer")
    }

    override fun plus(lexer: Lexer): Lexer {
        return LexerPlus(mutableListOf(lexer))
    }

    override fun or(lexer: Lexer): Lexer {
        return LexerOr(mutableListOf(lexer))
    }
}