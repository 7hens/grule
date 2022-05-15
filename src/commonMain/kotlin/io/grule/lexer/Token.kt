package io.grule.lexer

open class Token(
    val scanner: Scanner,
    val text: String,
    val position: TextPosition,
) {
    override fun toString(): String {
        return "'$text' [$position] <$scanner>"
    }
}
