package io.grule.matcher

import io.grule.token.MatcherContext

/**
 * non-greedy: a*?b
 */
internal class MatcherUntilNonGreedy(
    val matcher: Matcher, val terminal: Matcher, val minTimes: Int, val maxTimes: Int
) : Matcher {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(context: MatcherContext, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            if (repeatTimes >= minTimes) {
                try {
                    result += terminal.match(context, offset + result)
                    return result
                } catch (_: MatcherException) {
                }
            }
            result += matcher.match(context, offset + result)
            repeatTimes++
            if (repeatTimes > maxTimes) {
                throw MatcherException("limit out of range $maxTimes")
            }
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$matcher|$minTimes,$maxText*?$terminal}"
    }
}