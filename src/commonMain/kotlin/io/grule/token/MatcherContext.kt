package io.grule.token

interface MatcherContext {
    val position: TextPosition

    fun peek(offset: Int): Char?

    fun getText(start: Int, end: Int): String
}
