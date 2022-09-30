package io.grule.lexer

internal object LexerMatcherSot : LexerMatcher {

    override fun match(status: LexerStatus): LexerStatus {
        if (status.position <= status.data.charIndex) {
            return status
        }
        status.panic(this)
    }

    override fun toString(): String {
        return "SOT"
    }
}