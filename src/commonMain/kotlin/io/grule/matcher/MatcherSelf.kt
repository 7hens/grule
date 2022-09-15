package io.grule.matcher

// exp self { me + it or it + me }
internal class MatcherSelf<T : Matcher.Status<T>>(
    val primary: Matcher<T>, fn: Matcher.Self<T>.() -> Matcher<T>
) : Matcher<T> {
    private val repeatable by lazy { fn(SelfImpl(ItMatcher(), MeMatcher())) }

    override fun match(status: T): T {
        return try {
            match(status, repeatable)
        } catch (e: MatcherException) {
            match(status, primary)
        }
    }

    private fun match(status: T, primitiveMatcher: Matcher<T>): T {
        var result = status.apply(primitiveMatcher)
        while (true) {
            try {
                result = result.apply(repeatable)
            } catch (_: MatcherException) {
                return result
            }
        }
    }

    override fun toString(): String {
        return "($primary): $repeatable"
    }

    class SelfImpl<T : Matcher.Status<T>>(override val it: Matcher<T>, override val me: Matcher<T>) : Matcher.Self<T>

    interface Interface

    inner class ItMatcher(val isHead: Boolean = false) : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(primary)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = lastMatcher === primary
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(ItMatcher(true), matcher)
        }

        override fun toString(): String {
            return "\$it"
        }
    }

    inner class MeMatcher(val isHead: Boolean = false) : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(this@MatcherSelf)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = lastMatcher.let { it === primary || it === repeatable || it is MeMatcher }
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(MeMatcher(true), matcher)
        }

        override fun toString(): String {
            return "\$me"
        }
    }
}