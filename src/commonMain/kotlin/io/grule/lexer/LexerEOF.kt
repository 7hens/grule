package io.grule.lexer

internal object LexerEOF : Lexer {
    override fun match(context: LexerContext, offset: Int): Int {
        val c = context.peek(offset) ?: return 0
        throw LexerException("Unmatched char $c")
    }

    override fun toString(): String {
        return "EOF"
    }
}