package io.grule.lexer

internal object LexerMatcherEOF : LexerMatcher {
    override fun match(status: LexerMatcherStatus): LexerMatcherStatus {
        status.peek() ?: return status
        status.panic(this)
    }

    override fun toString(): String {
        return "EOF"
    }
}