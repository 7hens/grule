package io.grule.matcher

@Suppress("MemberVisibilityCanBePrivate")
fun interface Matcher<T : Matcher.Status<T>> {

    val isNode: Boolean get() = false

    fun match(status: T): T

    fun not(): Matcher<T> {
        return MatcherNot(this)
    }

    fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        return MatcherTimes(this, minTimes, maxTimes)
    }

    operator fun times(times: IntRange): Matcher<T> {
        return times(times.first, times.last)
    }

    operator fun times(times: Int): Matcher<T> {
        return times(times, times)
    }

    infix fun repeat(maxTimes: Int): Matcher<T> {
        return times(0, maxTimes)
    }

    fun repeat(): Matcher<T> {
        return repeat(Int.MAX_VALUE)
    }

    infix fun more(minTimes: Int): Matcher<T> {
        return times(minTimes, Int.MAX_VALUE)
    }

    fun more(): Matcher<T> {
        return more(1)
    }

    fun optional(): Matcher<T> {
        return times(0, 1)
    }

    infix fun join(separator: Matcher<T>): Matcher<T> {
        return repeat().join(separator)
    }

    infix fun interlace(separator: Matcher<T>): Matcher<T> {
        return separator.optional() + join(separator) + separator.optional()
    }

    infix fun until(terminal: Matcher<T>): Matcher<T> {
        return repeat().until(terminal)
    }

    infix fun till(terminal: Matcher<T>): Matcher<T> {
        return repeat().till(terminal)
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

        fun <T : Status<T>> shadow(): Matcher<T> {
            return MatcherShadow()
        }
    }

    interface Status<T : Status<T>> : ContextOwner {
        fun apply(matcher: Matcher<T>): T

        fun next(): T

        fun self(): T

        fun panic(rule: Any): Nothing

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
        fun set(value: T?)
        fun set(fn: (T) -> T) {
            set(fn(get()!!))
        }
    }

    interface Self<T : Status<T>> {
        val me: Matcher<T>
        val it: Matcher<T>
    }
}
