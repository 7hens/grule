package io.grule.matcher2

// exp self { me + it or it + me }
internal class MatcherSelf<T : Matcher.Status<T>>(
    val primary: Matcher<T>, fn: Matcher.Self<T>.() -> Matcher<T>
) : Matcher<T> {
    private val selfMatcher by lazy { fn(SelfImpl(NonRecursiveMatcher(primary), NonRecursiveMatcher(this))) }

    override fun match(status: T): T {
        var result = status.apply(primary)
        while (true) {
            try {
                result = status.apply(selfMatcher)
            } catch (_: Exception) {
                return result
            }
        }
    }

    class SelfImpl<T : Matcher.Status<T>>(override val it: Matcher<T>, override val me: Matcher<T>) : Matcher.Self<T>

    private class NonRecursiveMatcher<T : Matcher.Status<T>>(val matcher: Matcher<T>) : Matcher<T> {
        override fun match(status: T): T {
            if (status.lastMatcher === matcher) {
                return status
            }
            return status.apply(matcher)
        }
    }
}