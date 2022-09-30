package io.grule.lexer

internal object LexerMatcherBoundary : LexerMatcher {
    private val matcher = LexerDsl { WORD + WORD.not() or WORD.not() + WORD }

    override fun match(status: LexerStatus): LexerStatus {
        val isHead = status.position <= status.data.charIndex
        val isTail = status.peek() == null
        if (isHead || isTail) {
            return status
        }
        matcher.match(status.move(-1))
        return status
    }

    override fun toString(): String {
        return "\\b"
    }
}