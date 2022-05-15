package io.grule.matcher

internal object MatcherEOF : Matcher {
    override fun match(context: MatcherContext, offset: Int): Int {
        val c = context.peek(offset) ?: return 0
        throw MatcherException("Unmatched char $c")
    }

    override fun toString(): String {
        return "EOF"
    }
}