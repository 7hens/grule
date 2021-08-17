package io.grule.lexer

internal class LexerString(private val text: String) : Lexer() {

    override fun match(input: CharStream, offset: Int): Int {
        input.peek(offset + text.length)
        if (input.peek(offset) == CharStream.EOF) {
            throw LexerException("expected \"$text\" at ${input.index()}, actual is <EOF>")
        }
        if (input.getText(offset, offset + text.length) == text) {
            return text.length
        }
        throw LexerException("Unmatched text \"$text\", current char is '${input.peek(offset).toChar()}'")
    }
}