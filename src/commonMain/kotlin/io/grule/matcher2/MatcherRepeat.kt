package io.grule.matcher2

internal class MatcherRepeat<T>(val matcher: Matcher<T>, val minTimes: Int, val maxTimes: Int) : Matcher<T>{

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(context: T, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            try {
                result += matcher.match(context, offset + result)
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
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher|$minTimes,$maxText}"
    }
}