package io.grule.matcher2

@Suppress("MemberVisibilityCanBePrivate")
fun interface Matcher<T : Matcher.Status<T>> {

    fun match(status: T): T

    fun not(): Matcher<T> {
        return MatcherNot(this)
    }

    fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        return MatcherRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Matcher<T>, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        return (this + separator).untilGreedy(this, min, max)
    }

    fun optional(): Matcher<T> {
        return repeat(0, 1)
    }

    fun interlace(separator: Matcher<T>): Matcher<T> {
        return separator.optional() + join(separator) + separator.optional()
    }

    fun untilGreedy(terminal: Matcher<T>, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        return MatcherUntilGreedy(this, terminal, minTimes, maxTimes)
    }

    fun untilNonGreedy(terminal: Matcher<T>, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        return MatcherUntilNonGreedy(this, terminal, minTimes, maxTimes)
    }

    fun test(): Matcher<T> {
        return MatcherTest(this)
    }

    operator fun plus(matcher: Matcher<T>): Matcher<T> {
        return MatcherPlus(this, matcher)
    }

    infix fun or(matcher: Matcher<T>): Matcher<T> {
        return MatcherOr(this, matcher)
    }

    infix fun self(fn: Self<T>.() -> Matcher<T>): Matcher<T> {
        return MatcherSelf(this, fn)
    }

    companion object {
        fun context(): Context {
            return MatcherContextImpl()
        }
    }

    interface Status<T : Status<T>> : ContextOwner {
        fun apply(matcher: Matcher<T>): T

        fun next(): T

        fun self(isMe: Boolean): T

        val lastMatcher: Prop<Matcher<T>> get() = prop("lastMatcher")

        val parentMatcher: Prop<Matcher<T>> get() = prop("parentMatcher")
    }

    interface Context {
        fun <V> prop(key: String): Prop<V>
    }

    interface ContextOwner : Context {
        val context: Context

        override fun <V> prop(key: String): Prop<V> = context.prop(key)
    }

    interface Prop<T> {
        val key: String
        fun get(): T?
        fun set(value: T)
    }

    interface Self<T : Status<T>> {
        val me: Matcher<T>
        val it: Matcher<T>
    }
}
