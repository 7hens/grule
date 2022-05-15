package io.grule.matcher

/**
 * greedy: a*b
 */
internal class MatcherUntilGreedy(
    val matcher: Matcher, val terminal: Matcher, val minTimes: Int, val maxTimes: Int) : Matcher {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(context: MatcherContext, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        var lastResult = 0
        while (true) {
            try {
                lastResult = result
                result += matcher.match(context, offset + result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    throw MatcherException()
                }
            } catch (matcherException: MatcherException) {
                require(repeatTimes >= minTimes, matcherException)
                try {
                    result += terminal.match(context, offset + result)
                } catch (terminalError: MatcherException) {
                    require(repeatTimes - 1 >= minTimes, terminalError)
                    result = lastResult + terminal.match(context, offset + lastResult)
                }
                return result
            }
        }
    }

    private fun require(condition: Boolean, exception: MatcherException) {
        if (!condition) {
            throw exception
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher|$minTimes,$maxText*$terminal}"
    }
}