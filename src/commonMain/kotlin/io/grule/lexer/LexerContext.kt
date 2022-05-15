package io.grule.lexer

interface LexerContext {
    val position: TextPosition

    fun peek(offset: Int): Char?

    fun getText(start: Int, end: Int): String
}
