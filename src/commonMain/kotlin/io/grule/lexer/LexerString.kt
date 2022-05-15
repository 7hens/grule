package io.grule.lexer

internal class LexerString(private val text: String) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        charStream.peek(offset + text.length)
        if (charStream.peek(offset) == null) {
            throw LexerException(charStream, text, "<EOF>")
        }
        val actualText = charStream.getText(offset, offset + text.length)
        if (actualText == text) {
            return text.length
        }
        throw LexerException(charStream, text, actualText)
    }

    override fun toString(): String {
        return text
    }
}
