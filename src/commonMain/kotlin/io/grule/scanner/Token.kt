package io.grule.scanner

import io.grule.lexer.TextPosition

open class Token(
    val scanner: Scanner,
    val text: String,
    val position: TextPosition,
) {
    override fun toString(): String {
        return "'$text' [$position] <$scanner>"
    }
}
