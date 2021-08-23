package io.grule.lexer

internal class LexerString(private val text: String) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        charStream.peek(offset + text.length)
        if (charStream.peek(offset) == CharStream.EOF) {
            throw LexerException("expected \"$text\" at ${charStream.charIndex}, actual is <EOF>")
        }
        if (charStream.getText(offset, offset + text.length) == text) {
            return text.length
        }
        throw LexerException("Unmatched text \"$text\", current char is '${charStream.peek(offset).toChar()}'")
    }
}
