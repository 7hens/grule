package io.grule.lexer

internal object LexerEOF : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        val c = charStream.peek(offset) ?: return 0
        throw LexerException("Unmatched char $c")
    }

    override fun toString(): String {
        return "EOF"
    }
}