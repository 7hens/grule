package io.grule.matcher

import io.grule.node.KeyProvider

internal class KeyMatcherImpl<T : Matcher.Status<T>>(
    keyProvider: KeyProvider,
    initMatcher: Matcher<T> = MatcherShadow()
) : KeyMatcher<T>, KeyProvider by keyProvider {

    override val isEmpty: Boolean get() = delegate.isEmpty

    var delegate: Matcher<T> = initMatcher.wrap()
        private set(value) {
            field = value.wrap()
        }

    override fun match(status: T): T {
        return delegate.match(status)
    }

    override fun not(): Matcher<T> {
        delegate = delegate.not()
        return this
    }

    override fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        delegate = delegate.times(minTimes, maxTimes)
        return this
    }

    override fun join(separator: Matcher<T>): Matcher<T> {
        delegate = delegate.join(separator)
        return this
    }

    override fun interlace(separator: Matcher<T>): Matcher<T> {
        delegate = delegate.interlace(separator)
        return this
    }

    override fun until(terminal: Matcher<T>): Matcher<T> {
        delegate = delegate.until(terminal)
        return this
    }

    override fun till(terminal: Matcher<T>): Matcher<T> {
        delegate = delegate.till(terminal)
        return this
    }

    override fun test(): Matcher<T> {
        delegate = delegate.test()
        return this
    }

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        delegate = delegate.plus(matcher)
        return this
    }

    override fun or(matcher: Matcher<T>): Matcher<T> {
        delegate = delegate.or(matcher)
        return this
    }

    override fun self(fn: Matcher.Self<T>.() -> Matcher<T>): Matcher<T> {
        delegate = delegate.self(fn)
        return this
    }

    private fun <T : Matcher.Status<T>> Matcher<T>.wrap(): Matcher<T> {
        return if (this is KeyMatcherImpl<T>) Wrapper(this) else this
    }

    override fun toString(): String {
        return "$delegate"
    }

    private class Wrapper<T : Matcher.Status<T>>(val matcher: KeyMatcher<T>) : KeyMatcher<T> by matcher {
        override fun toString(): String {
            return matcher.toString()
        }
    }
}