package io.grule.matcher2.lexer

import io.grule.matcher.TextPosition

interface LexerMatcherContext {
    val position: TextPosition

    fun peek(offset: Int): Char?

    fun getText(start: Int, end: Int): String
}
