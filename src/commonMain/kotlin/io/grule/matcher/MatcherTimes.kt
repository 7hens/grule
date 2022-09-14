package io.grule.matcher

internal class MatcherTimes<T : Matcher.Status<T>>(
    val matcher: Matcher<T>, val minTimes: Int, val maxTimes: Int
) : Matcher<T> {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(status: T): T {
        var result = status
        var repeatTimes = 0
        while (true) {
            try {
                result = result.apply(matcher)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    return result
                }
            } catch (e: MatcherException) {
                if (repeatTimes < minTimes) {
                    throw e
                }
                return result
            }
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes != Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher * |$minTimes,$maxText}"
    }

    override fun join(separator: Matcher<T>): Matcher<T> {
        if (maxTimes == 1) {
            return this
        }
        val min = maxOf(0, minTimes - 1)
        val max = maxOf(0, maxTimes - 1)
        val composedMatcher = (matcher + separator).times(min, max).till(matcher)
        return if (minTimes > 0) composedMatcher else composedMatcher.optional()
    }

    override fun until(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilNonGreedy(matcher, terminal, minTimes, maxTimes)
    }

    override fun till(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilGreedy(matcher, terminal, minTimes, maxTimes)
    }
}