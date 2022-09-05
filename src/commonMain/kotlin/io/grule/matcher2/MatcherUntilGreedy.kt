package io.grule.matcher2

/**
 * greedy: a*b
 */
internal class MatcherUntilGreedy<T : Matcher.Status<T>>(
    val matcher: Matcher<T>, val terminal: Matcher<T>,
    val minTimes: Int, val maxTimes: Int
) : Matcher<T> {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(status: T): T {
        var result = status
        var lastResult = status
        var repeatTimes = 0
        while (true) {
            try {
                lastResult = result
                result = result.apply(matcher)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    throw MatcherException("limit out of range $maxTimes")
                }
            } catch (matcherException: MatcherException) {
                require(repeatTimes >= minTimes, matcherException)
                return try {
                    result.apply(terminal)
                } catch (terminalError: MatcherException) {
                    require(repeatTimes - 1 >= minTimes, terminalError)
                    lastResult.apply(terminal)
                }
            }
        }
    }

    private fun require(condition: Boolean, exception: MatcherException) {
        if (!condition) {
            throw exception
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes != Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher * $terminal |$minTimes,$maxText}"
    }
}