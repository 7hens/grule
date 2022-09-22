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

    private val itMatcher = ItMatcher()

    private val meMatcher = MeMatcher()

    private val repeatable by lazy { fn(Matcher.Self(meMatcher, itMatcher)) }

    override fun match(status: T): T {
        return meMatcher.match(status)
    }

    private fun matchInternal(status: T, head: Matcher<T>, body: Matcher<T>): T {
        var result = head.match(status).withKey(head)
        try {
            while (true) {
                result = body.match(result).withKey(body)
            }
        } catch (_: MatcherException) {
        }
        return result
    }

    override fun toString(): String {
        return "($primary): $repeatable"
    }

    inner class MeMatcher : Matcher<T> {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            return try {
                status.self(repeatable)
            } catch (e: MatcherException) {
                status.self(primary)
//                primary.match(status)
//                primary.match(status)
            }
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return HeadMeMatcher(matcher)
        }

        override fun toString(): String {
            return "\$me"
        }
    }

    inner class HeadMeMatcher(val body: Matcher<T>) : Matcher<T> {
        override fun match(status: T): T {
            var result = status.self(primary)
            try {
                while (true) {
                    result = result.selfPartial(body)
                }
            } catch (_: MatcherException) {
            }
            return result
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return HeadMeMatcher(body + matcher)
        }

        override fun or(matcher: Matcher<T>): Matcher<T> {
            if (matcher is HeadMeMatcher) {
                return HeadMeMatcher(body or matcher.body)
            }
            return super.or(matcher)
        }

        override fun toString(): String {
            return "\$me $body"
        }
    }

    inner class ItMatcher : Matcher<T> {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            return status.self(primary)
        }

        override fun toString(): String {
            return "\$it"
        }
    }
}