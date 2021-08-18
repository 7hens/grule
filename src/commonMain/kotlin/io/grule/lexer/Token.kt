package io.grule.lexer

open class Token(
    val lexer: Lexer,
    val text: String,
    val line: Int,
    val column: Int,
    val charIndex: Int,
) {

    constructor(lexer: Lexer, text: String, stream: CharStream)
            : this(lexer, text, stream.line, stream.column, stream.charIndex)

    override fun toString(): String {
        return "'$text' [$line:$column] $lexer"
    }
}
