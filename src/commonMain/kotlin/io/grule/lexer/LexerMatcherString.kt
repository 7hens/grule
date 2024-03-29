package io.grule.lexer

internal class LexerMatcherString(private val text: String) : LexerMatcher {

    override fun match(status: LexerStatus): LexerStatus {
        status.peek(text.length)
        if (status.peek() == null) {
            status.panic(LexerMatcherEOF)
        }
        val actualText = status.getText(text.length)
        if (actualText == text) {
            return status.next(text.length)
        }
        status.panic(this)
    }

    override fun toString(): String {
        return text
    }
}
