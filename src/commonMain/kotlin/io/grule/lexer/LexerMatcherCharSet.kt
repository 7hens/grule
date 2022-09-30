package io.grule.lexer

internal class LexerMatcherCharSet(val set: Iterable<Char>) : LexerMatcher {

    override fun match(status: LexerStatus): LexerStatus {
        val c = status.peek() ?: status.panic(LexerMatcherEOF)
        if (c in set) {
            return status.move()
        }
        status.panic(this)
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
        val ANY = LexerMatcherCharSet(Char.MIN_VALUE..Char.MAX_VALUE)
    }
}