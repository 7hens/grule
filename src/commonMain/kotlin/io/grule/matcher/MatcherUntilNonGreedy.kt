package io.grule.matcher

/**
 * non-greedy: a*?b
 */
internal class MatcherUntilNonGreedy<T : Status<T>>(
    val matcher: Matcher<T>,
    val times: CountRange,
    val terminal: Matcher<T>,
) : Matcher<T> {

    override fun match(status: T): T {
        var result = status
        for (i in 0 until times.min) {
            result = matcher.match(result)
        }
        try {
            return terminal.match(result)
        } catch (_: MatcherException) {
        }
        for (i in 0 until times.length) {
            result = matcher.match(result)
            try {
                return terminal.match(result)
            } catch (_: MatcherException) {
            }
        }
        status.panic(this)
    }

    override fun toString(): String {
        return "($matcher *?|$times| $terminal)"
    }
}