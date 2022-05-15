package io.grule.lexer

internal class LexerCharSet(val set: Iterable<Char>) : Lexer {
    override fun match(context: LexerContext, offset: Int): Int {
        val c = context.peek(offset)
            ?: throw LexerException(context, this, Lexer.EOF)
        if (c in set) {
            return 1
        }
        throw LexerException(context, toString(), c.toString())
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