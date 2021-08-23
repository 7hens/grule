package io.grule.lexer

internal object LexerEOF : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        val c = charStream.peek(0)
        if (c == CharStream.EOF) {
            return 0
        }
        throw LexerException("Unmatched char $c")
    }

    override fun toString(): String {
        return "EOF"
    }
}