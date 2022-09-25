package io.grule.token

import io.grule.lexer.Lexer

open class Token(
    val lexer: Lexer,
    val text: String,
    val position: TextPosition,
) {
    override fun toString(): String {
        return "'$text' [$position] <$lexer>"
    }
}
