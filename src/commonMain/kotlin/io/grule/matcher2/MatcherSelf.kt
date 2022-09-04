package io.grule.matcher2

internal class MatcherSelf<T>(val primary: Matcher<T>, fn: Matcher.Self<T>.() -> Matcher<T>) : Matcher<T> {
    private val selfParser by lazy { fn(SelfImpl(primary, this)) }

    override fun match(status: T): T {
        var result = primary.match(status)
        while (true) {
            try {
                result = selfParser.match(result)
            } catch (_: Exception) {
                return result
            }
        }
    }

    class SelfImpl<T>(override val it: Matcher<T>, override val me: Matcher<T>) : Matcher.Self<T>
}