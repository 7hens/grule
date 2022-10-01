package io.grule.matcher

internal class MatcherMove<T : Status<T>>(val step: Int) : Matcher<T> {

    override fun match(status: T): T {
        return status.move(step)
    }

    override fun matchesEmpty(): Boolean {
        return step == 0
    }

    override fun plus(step: Int): Matcher<T> {
        return MatcherMove(this.step + step)
    }

    override fun times(minTimes: Int, maxTimes: Int): Matcher<T> {
        if (step == 0) {
            return this
        }
        return super.times(minTimes, maxTimes)
    }

    override fun toString(): String {
        return "+$step"
    }
}