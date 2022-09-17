package io.grule.matcher

/**
 * non-greedy: a*?b
 */
internal class MatcherUntilNonGreedy<T : Status<T>>(
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
        for (i in minTimes..maxTimes) {
            try {
                return result.apply(terminal)
            } catch (_: MatcherException) {
            }
            result = result.apply(matcher)
        }
        status.panic(this)
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "($matcher *|$minTimes,$maxText|? $terminal)"
    }
}