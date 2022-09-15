package io.grule.matcher

internal open class MatcherBuilder<T : Matcher.Status<T>>(initMatcher: Matcher<T> = MatcherShadow()) : Matcher<T> {
    var delegate: Matcher<T> = initMatcher
        private set

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
}