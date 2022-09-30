package io.grule.lexer

internal class LexerMatcherString(private val text: String) : LexerMatcher {

    override fun match(status: LexerStatus): LexerStatus {
        status.peek(text.length)
        if (status.peek() == null) {
            status.panic(LexerMatcherEof)
        }
        val actualText = status.getText(text.length)
        if (actualText == text) {
            return status.move(text.length)
        }
        status.panic(this)
    }

    override fun toString(): String {
        return text
    }
}
