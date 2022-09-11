package io.grule.matcher

internal class MatcherRepeat<T : Matcher.Status<T>>(
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
}