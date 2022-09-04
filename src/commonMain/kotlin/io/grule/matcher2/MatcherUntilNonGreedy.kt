package io.grule.matcher2

/**
 * non-greedy: a*?b
 */
internal class MatcherUntilNonGreedy<T>(
    val matcher: Matcher<T>, val terminal: Matcher<T>,
    val minTimes: Int, val maxTimes: Int
) : Matcher<T> {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(status: T): T {
        var result = status
        var repeatTimes = 0
        while (true) {
            if (repeatTimes >= minTimes) {
                try {
                    return terminal.match(result)
                } catch (_: MatcherException) {
                }
            }
            result = matcher.match(result)
            repeatTimes++
            if (repeatTimes > maxTimes) {
                throw MatcherException("limit out of range $maxTimes")
            }
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher *? $terminal |$minTimes,$maxText}"
    }
}