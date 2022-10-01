package io.grule.token

import io.grule.lexer.Lexer

open class Token(
    val lexer: Lexer,
    val text: String,
    val textRange: TextRange,
) {

    val position: TextPosition get() = textRange.start

    override fun toString(): String {
        return "#[${textRange.start}, ${textRange.end}] <$lexer>    $text"
    }
}
