package io.grule.matcher

internal class MatcherTimes<T : Status<T>>(
    val matcher: Matcher<T>,
    val times: CountRange,
) : Matcher<T> {

    override fun match(status: T): T {
        var result = status
        for (i in 0 until times.min) {
            result = matcher.match(result)
        }
        try {
            for (i in 0 until times.length) {
                result = matcher.match(result)
            }
        } catch (_: MatcherException) {
        }
        return result
    }

    override fun matchesEmpty(): Boolean {
        return times.min == 0 || matcher.matchesEmpty()
    }

    override fun toString(): String {
        return "($matcher * ${times})"
    }

    override fun join(separator: Matcher<T>): Matcher<T> {
        if (times.isSingle) {
            return this
        }
        val composedMatcher = (matcher + separator).times((times - 1).toIntRange()).till(matcher)
        return if (times.min > 0) composedMatcher else composedMatcher.optional()
    }

    override fun until(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilNonGreedy(matcher, times, terminal)
    }

    override fun till(terminal: Matcher<T>): Matcher<T> {
        return MatcherUntilGreedy(matcher, times, terminal)
    }
}