package io.grule.matcher

internal class MatcherTimes<T : Status<T>>(
    val matcher: Matcher<T>,
    val minTimes: Int,
    val maxTimes: Int,
) : Matcher<T> {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(status: T): T {
        var result = status
        for (i in 0 until minTimes) {
            result = result.apply(matcher)
        }
        try {
            for (i in minTimes until maxTimes) {
                result = result.apply(matcher)
            }
        } catch (_: MatcherException) {
        }
        return result
    }

    override fun toString(): String {
        if (minTimes == maxTimes) {
            return "($matcher *$minTimes)"
        }
        if (minTimes == 0 && maxTimes == 1) {
            return "($matcher *?)"
        }
        val maxText = if (maxTimes != Int.MAX_VALUE) "$maxTimes" else ""
        return "($matcher *:$minTimes,$maxText)"
    }

    override fun join(separator: Matcher<T>): Matcher<T> {
        if (maxTimes == 1) {
            return this
        }
        val min = maxOf(0, minTimes - 1)
        val max = if (maxTimes == Int.MAX_VALUE) maxTimes else maxOf(0, maxTimes - 1)
        val composedMatcher = (matcher + separator).times(min, max).till(matcher)
        return if (minTimes > 0) composedMatcher else composedMatcher.optional()
    }

    override fun until(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilNonGreedy(matcher, minTimes, maxTimes, terminal)
    }

    override fun till(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilGreedy(matcher, minTimes, maxTimes, terminal)
    }

    override fun plus(matcher: Matcher<T>): Matcher<T> {
        return till(matcher)
    }
}