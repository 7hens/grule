package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherException

internal class LexerMatcherCharSet(val set: Iterable<Char>) : LexerMatcher {

    override fun match(context: LexerMatcherContext, offset: Int): Int {
        val c = context.peek(offset) ?: throw MatcherException(context, offset)
        if (c in set) {
            return 1
        }
        throw MatcherException(context, offset)
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