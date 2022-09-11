package io.grule.matcher

import io.grule.token.MatcherContext

internal class MatcherCharSet(val set: Iterable<Char>) : Matcher {
    override fun match(context: MatcherContext, offset: Int): Int {
        val c = context.peek(offset)
            ?: throw MatcherException(context, this, Matcher.EOF)
        if (c in set) {
            return 1
        }
        throw MatcherException(context, toString(), c.toString())
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
        val ANY = MatcherCharSet(Char.MIN_VALUE..Char.MAX_VALUE)
    }
}