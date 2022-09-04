package io.grule.matcher2

/**
 * greedy: a*b
 */
internal class MatcherUntilGreedy<T>(
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
                result = matcher.match(result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    throw MatcherException("limit out of range $maxTimes")
                }
            } catch (matcherException: MatcherException) {
                require(repeatTimes >= minTimes, matcherException)
                return try {
                    terminal.match(result)
                } catch (terminalError: MatcherException) {
                    require(repeatTimes - 1 >= minTimes, terminalError)
                    terminal.match(lastResult)
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