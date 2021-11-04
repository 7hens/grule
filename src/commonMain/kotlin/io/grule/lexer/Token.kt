package io.grule.lexer

open class Token(
    val scanner: Scanner,
    val text: String,
    val line: Int,
    val column: Int,
    val charIndex: Int,
) {

    constructor(scanner: Scanner, text: String, stream: CharStream)
            : this(scanner, text, stream.line, stream.column, stream.charIndex)

    override fun toString(): String {
        return "'$text' [$line:$column] $scanner"
    }
}
