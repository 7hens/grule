package io.grule.matcher

import io.grule.node.KeyProvider

// exp self { me + it or it + me }
internal class MatcherSelf<T : Matcher.Status<T>>(
    primary: Matcher<T>, fn: Matcher.Self<T>.() -> Matcher<T>
) : Matcher<T> {

    private var nonRepeatable = recursive()
    private var repeatable = recursive()

    init {
        nonRepeatable or primary.key(this)
        resolve(fn)
    }

    private val selfMatcher by lazy { build() }

    override fun match(status: T): T {
        println("selfMatcher: $selfMatcher")
        return status.apply(selfMatcher)
    }

    override fun toString(): String {
        return "($nonRepeatable): $repeatable"
    }

    override fun self(fn: Matcher.Self<T>.() -> Matcher<T>): Matcher<T> {
        resolve(fn)
        return this
    }

    private fun resolve(fn: Matcher.Self<T>.() -> Matcher<T>) {
        val result = fn(SelfImpl(ItMatcher(build(), false), MeMatcher(nonRepeatable, false)))
        if (result is KeyMatcher<T> && result.key == this) {
            repeatable or result
        } else {
            nonRepeatable or result
        }
    }

    fun build(): Matcher<T> {
        if (repeatable.isEmpty) {
            return recursive() + nonRepeatable
        }
        return recursive() + nonRepeatable.key(this) + repeatable.repeat().key(this) or repeatable.more().key(this)
    }

    fun recursive(): Matcher<T> {
        return Matcher.shadow<T>().key(this)
    }

    fun isRecursive(matcher: Matcher<*>): Boolean {
        return KeyProvider.keyOf(matcher) === this
    }

    class SelfImpl<T : Matcher.Status<T>>(override val it: Matcher<T>, override val me: Matcher<T>) : Matcher.Self<T>

    interface Interface

    inner class ItMatcher(val delegate: Matcher<T>, val isHead: Boolean) : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(delegate)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = isRecursive(lastMatcher)
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return MatcherPlus(ItMatcher(delegate, true), matcher)
        }

        override fun toString(): String {
            return "\$it"
        }
    }

    inner class MeMatcher(val delegate: Matcher<T>, val isHead: Boolean) : Matcher<T>, Interface {
        override val isNode: Boolean = true

        override fun match(status: T): T {
            if (!isHead) {
                return status.apply(delegate)
            }
            val lastMatcher = status.lastMatcher.get() ?: status.panic(this)
            val isSelf = isRecursive(lastMatcher)
            println("lastMatcher: $lastMatcher")
            return if (isSelf) status.self() else status.panic(this)
        }

        override fun plus(matcher: Matcher<T>): Matcher<T> {
            return recursive() + MatcherPlus(MeMatcher(delegate, true), matcher)
        }

        override fun toString(): String {
            return "\$me"
        }
    }
}