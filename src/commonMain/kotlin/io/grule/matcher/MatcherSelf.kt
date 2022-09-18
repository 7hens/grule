package io.grule.matcher

// exp self { me + it or it + me }
/**
 * x self { a + me + b }
 * x self { a + me + b + me + c }
 */
internal class MatcherSelf<T : Status<T>>(
    val primary: Matcher<T>,
    val fn: Matcher.Self<T>.() -> Matcher<T>,
) : Matcher<T> {

    private val resultMatcher by lazy {
        val itMatcher = ItMatcher(primary, false)
        val meMatcher = MeMatcher(this, false)
        fn(Matcher.Self(meMatcher, itMatcher))
    }

    private val selfMatcher by lazy {
        resultMatcher.more() or primary + resultMatcher.repeat()
    }

    override fun match(status: T): T {
        println("selfMatcher: $selfMatcher")
        return status.apply(selfMatcher)
    }

    override fun toString(): String {
        return "($primary): $resultMatcher"
    }

    inner class ItMatcher(val delegate: Matcher<T>, val isHead: Boolean) : Matcher<T> {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(delegate)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = lastMatcher === delegate
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(ItMatcher(delegate, true), matcher)
        }

        override fun toString(): String {
            return "\$it"
        }
    }

    inner class MeMatcher(val delegate: Matcher<T>, val isHead: Boolean) : Matcher<T> {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(delegate)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = lastMatcher === primary || lastMatcher === resultMatcher || lastMatcher is MeMatcher
            println("me:: lastMatcher: $lastMatcher")
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(MeMatcher(delegate, true), matcher)
        }

        override fun toString(): String {
            return "\$me"
        }
    }
}