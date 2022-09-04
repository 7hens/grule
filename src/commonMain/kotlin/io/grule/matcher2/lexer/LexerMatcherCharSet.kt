package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal class LexerMatcherCharSet(val set: Iterable<Char>) : LexerMatcher {

    override fun match(status: LexerMatcherContext): LexerMatcherContext {
        val c = status.peek() ?: throw MatcherException(status)
        if (c in set) {
            return status.next()
        }
        throw MatcherException(status)
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