package io.grule.lexer

open class Token(
    val lexer: Lexer,
    val text: String,
    val line: Int,
    val column: Int,
    val firstCharIndex: Int = -1,
    val lastCharIndex: Int = -1
) {
    var extra: Any? = null

    override fun toString(): String {
        return "\"$text\" [$line:$column] $lexer"
    }

    companion object {
        operator fun invoke(lexer: Lexer, text: String, stream: CharStream): Token {
            val firstCharIndex = stream.index()
            val lastCharIndex = firstCharIndex + text.length
            return Token(lexer,  text, stream.line(), stream.column(), firstCharIndex, lastCharIndex)
        }
    }
}
