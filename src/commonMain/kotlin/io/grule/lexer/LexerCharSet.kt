package io.grule.lexer

internal class LexerCharSet(val set: Iterable<Char>) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        val c = charStream.peek(offset)
        if (c == CharStream.EOF) {
            throw LexerException("expected ${this}, actual is <EOF>")
        }
        if (c.toChar() in set) {
            return 1
        }
        throw LexerException("Unmatched char '${c.toChar()}' in $set")
    }

    override fun toString(): String {
        if (this == ANY) {
            return "[.]"
        }
        if (set is List<*>) {
            return "[${set.joinToString("")}]"
        }
        return "$set"
    }

    companion object {
        val ANY = LexerCharSet(Char.MIN_VALUE..Char.MAX_VALUE)
    }
}