package io.grule.matcher2

// exp self { me + it or it + me }
internal class MatcherSelf<T : Matcher.Status<T>>(
    val primary: Matcher<T>, fn: Matcher.Self<T>.() -> Matcher<T>
) : Matcher<T> {
    private val selfMatcher by lazy { fn(SelfImpl(ItMatcher(), MeMatcher())) }

    override fun match(status: T): T {
        var result = status.apply(primary)
        while (true) {
            try {
                result = result.apply(selfMatcher)
            } catch (_: MatcherException) {
                return result
            }
        }
    }

    override fun toString(): String {
        return "$primary : $selfMatcher"
    }

    class SelfImpl<T : Matcher.Status<T>>(override val it: Matcher<T>, override val me: Matcher<T>) : Matcher.Self<T>

    interface Interface

    inner class ItMatcher : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            val lastMatcher = status.lastMatcher.get()!!
            val isSelf = lastMatcher === primary
            return if (isSelf) status.self() else status.apply(primary)
        }

        override fun toString(): String {
            return "\$it"
        }
    }

    inner class MeMatcher : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            val self = this@MatcherSelf
            val lastMatcher = status.lastMatcher.get()!!
            val isSelf = lastMatcher === primary || lastMatcher === selfMatcher
            return if (isSelf) status.self() else status.apply(self)
        }

        override fun toString(): String {
            return "\$me"
        }
    }
}