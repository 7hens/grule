package io.grule.lexer

internal object LexerEof : Lexer {

    override fun lex(context: LexerContext) {
        Matcher.match(context)
        context.emit(this)
    }

    override fun toString(): String {
        return Matcher.toString()
    }

    object Matcher : LexerMatcher {
        override fun match(status: LexerStatus): LexerStatus {
            status.peek() ?: return status
            status.panic(this)
        }

        override fun toString(): String {
            return "EOF"
        }
    }
}
