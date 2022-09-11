package io.grule.matcher

import io.grule.token.MatcherContext

internal open class MatcherBuilder : Matcher {
    private var delegate: Matcher = MatcherShadow()

    override fun match(context: MatcherContext, offset: Int): Int {
        return delegate.match(context, offset)
    }

    override fun plus(matcher: Matcher): Matcher {
        delegate = delegate.plus(matcher)
        return this
    }

    override fun or(matcher: Matcher): Matcher {
        delegate = delegate.or(matcher)
        return this
    }

    override fun toString(): String {
        return delegate.toString()
    }

}