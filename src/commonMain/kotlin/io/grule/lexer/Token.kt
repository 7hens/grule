package io.grule.lexer

import io.grule.matcher.TextPosition

open class Token(
    val lexer: Lexer,
    val text: String,
    val position: TextPosition,
) {
    override fun toString(): String {
        return "'$text' [$position] <$lexer>"
    }
}
