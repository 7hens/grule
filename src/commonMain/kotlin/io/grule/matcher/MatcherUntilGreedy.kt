package io.grule.matcher

/**
 * greedy: a*b
 */
internal class MatcherUntilGreedy<T : Status<T>>(
    val matcher: Matcher<T>,
    val minTimes: Int,
    val maxTimes: Int,
    val terminal: Matcher<T>,
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
        return matchInternal(result, maxTimes - minTimes)
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
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "($matcher *?|$minTimes,$maxText|? $terminal)"
    }
}