package io.grule.matcher

import io.grule.node.KeyProvider

@Suppress("MemberVisibilityCanBePrivate")
fun interface Matcher<T : Status<T>> {

    val isNode: Boolean get() = false

    val isEmpty: Boolean get() = false

    fun match(status: T): T

    fun key(key: Any): KeyMatcher<T> {
        return KeyMatcherImpl(KeyProvider(key), this)
    }

    fun not(): Matcher<T> {
        return MatcherNot(this)
    }

    fun test(): Matcher<T> {
        return MatcherTest(this)
    }

    operator fun plus(matcher: Matcher<T>): Matcher<T> {
        if (matcher is ReversedMatcher<T>) {
            return matcher.reverser + this
        }
        return MatcherPlus(this, matcher)
    }

    infix fun or(matcher: Matcher<T>): Matcher<T> {
        if (matcher is ReversedMatcher<T>) {
            return matcher.reverser or this
        }
        return MatcherOr(this, matcher)
    }

    fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        return MatcherTimes(this, CountRange(minTimes, maxTimes))
    }

    operator fun times(times: IntRange): Matcher<T> {
        return times(times.first, times.last)
    }

    operator fun times(times: Int): Matcher<T> {
        return times(times, times)
    }

    infix fun repeat(maxTimes: Int): Matcher<T> {
        return times(0, maxTimes)
    }

    fun repeat(): Matcher<T> {
        return repeat(Int.MAX_VALUE)
    }

    infix fun more(minTimes: Int): Matcher<T> {
        return times(minTimes, Int.MAX_VALUE)
    }

    fun more(): Matcher<T> {
        return more(1)
    }

    fun optional(): Matcher<T> {
        return times(0, 1)
    }

    infix fun join(separator: Matcher<T>): Matcher<T> {
        if (separator is ReversedMatcher<T>) {
            return separator.reverser.join(this)
        }
        return (this + separator).till(this)
    }

    infix fun interlace(separator: Matcher<T>): Matcher<T> {
        if (separator is ReversedMatcher<T>) {
            return separator.reverser.interlace(this)
        }
        return separator.optional() + join(separator) + separator.optional()
    }

    infix fun until(terminal: Matcher<T>): Matcher<T> {
        if (terminal is ReversedMatcher<T>) {
            return terminal.reverser.until(this)
        }
        return MatcherUntilNonGreedy(this, CountRange.INFINITE, terminal)
    }

    infix fun till(terminal: Matcher<T>): Matcher<T> {
        if (terminal is ReversedMatcher<T>) {
            return terminal.reverser.until(this)
        }
        return MatcherUntilGreedy(this, CountRange.INFINITE, terminal)
    }

    infix fun self(fn: Self<T>.() -> Matcher<T>): Matcher<T> {
        val me = MatcherMe(shadow(), this, shadow())
        return fn(Self(me, this))
    }

    companion object {
        fun context(): Context {
            return MatcherContextImpl()
        }

        fun <T : Status<T>> shadow(): Matcher<T> {
            return MatcherShadow()
        }
    }

    data class Self<T : Status<T>>(
        val me: Matcher<T>,
        val it: Matcher<T>,
    )
}
