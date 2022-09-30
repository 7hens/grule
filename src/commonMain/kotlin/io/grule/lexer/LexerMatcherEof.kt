package io.grule.lexer

internal object LexerMatcherEof : LexerMatcher {
    override fun match(status: LexerStatus): LexerStatus {
        status.peek() ?: return status
        status.panic(this)
    }

    override fun toString(): String {
        return "EOF"
    }
}