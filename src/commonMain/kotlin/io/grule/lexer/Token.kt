package io.grule.lexer

open class Token(
    val scanner: Scanner,
    val text: String,
    val position: TextPosition,
) {

    constructor(scanner: Scanner, text: String, stream: CharStream)
            : this(scanner, text, stream.position)

    override fun toString(): String {
        return "'$text' [$position] <$scanner>"
    }
}
