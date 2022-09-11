package io.grule.matcher

import io.grule.token.MatcherContext

internal class MatcherNot(private val matcher: Matcher) : Matcher {

    override fun match(context: MatcherContext, offset: Int): Int {
        context.peek(offset)
            ?: throw MatcherException(context, matcher, Matcher.EOF)
        try {
            matcher.match(context, offset)
        } catch (e: MatcherException) {
            return 1
        }
        throw MatcherException()
    }

    override fun not(): Matcher {
        return matcher
    }

    override fun toString(): String {
        return "(! $matcher)"
    }
}