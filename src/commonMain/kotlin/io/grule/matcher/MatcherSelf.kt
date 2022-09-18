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

    private val repeatable by lazy {
        val meMatcher = MeMatcher(this, false)
        val itMatcher = ItMatcher(primary, false)
        fn(Matcher.Self(meMatcher, itMatcher))
    }

    override fun match(status: T): T {
        return try {
            matchInternal(status, repeatable, repeatable)
        } catch (_: MatcherException) {
            matchInternal(status, primary, repeatable)
        }
    }

    private fun matchInternal(status: T, head: Matcher<T>, body: Matcher<T>): T {
        var result = head.match(status).key(this)
        try {
            while (true) {
                result = body.match(result).key(this)
            }
        } catch (_: MatcherException) {
        }
        return result
    }

    private fun isSelf(status: T): Boolean = status.key === this

    override fun toString(): String {
        return "($primary): $repeatable"
    }

    inner class ItMatcher(val delegate: Matcher<T>, val isHead: Boolean) : Matcher<T> {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return delegate.match(status)
            }
            return if (isSelf(status)) status.self() else status.panic(this)
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
                return delegate.match(status)
            }
            return if (isSelf(status)) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(MeMatcher(delegate, true), matcher)
        }

        override fun toString(): String {
            return "\$me"
        }
    }
}