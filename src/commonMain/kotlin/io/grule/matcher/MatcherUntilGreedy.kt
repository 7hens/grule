package io.grule.matcher

/**
 * greedy: a*b
 */
internal class MatcherUntilGreedy<T : Status<T>>(
    val matcher: Matcher<T>,
    val times: CountRange,
    val terminal: Matcher<T>,
) : Matcher<T> {

    override fun match(status: T): T {
        var result = status
        for (i in 0 until times.min) {
            result = result.apply(matcher)
        }
        return matchInternal(result, times.length)
    }

    private fun matchInternal(status: T, restTimes: Int): T {
        if (restTimes == 0) {
            return status.apply(terminal)
        }
        return try {
            matchInternal(status.apply(matcher), restTimes - 1)
        } catch (e: MatcherException) {
            status.apply(terminal)
        }
    }

    override fun toString(): String {
        return "($matcher *|$times}| $terminal)"
    }
}