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
            result = matcher.match(result)
        }
        return matchInternal(result, times.length)
    }

    private fun matchInternal(status: T, restTimes: Int): T {
        if (restTimes == 0) {
            return terminal.match(status)
        }
        return try {
            matchInternal(matcher.match(status), restTimes - 1)
        } catch (e: MatcherException) {
            terminal.match(status)
        }
    }

    override fun toString(): String {
        return "($matcher *|$times}| $terminal)"
    }
}