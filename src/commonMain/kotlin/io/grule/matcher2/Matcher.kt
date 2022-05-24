package io.grule.matcher2

@Suppress("MemberVisibilityCanBePrivate")
fun interface Matcher<T> {
    fun match(context: T, offset: Int): Int

    fun match(context: T) = match(context, 0)

    fun not(): Matcher<T> {
        return MatcherNot(this)
    }

    fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        return MatcherRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Matcher<T>, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher<T> {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        return (builder() + this + separator).untilGreedy(this, min, max)
    }

    fun optional(): Matcher<T> {
        return repeat(0, 1)
    }

    fun interlace(separator: Matcher<T>): Matcher<T> {
        return builder() + separator.optional() + join(separator) + separator.optional()
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

    private fun <R : MatcherBuilder<T, R>> builder(): MatcherBuilder<T, R> {
        return MatcherBuilder()
    }
}
