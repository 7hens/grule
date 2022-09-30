package io.grule.matcher

interface Status<T : Status<T>> {

    @Deprecated("Use move() instead", ReplaceWith("move()"))
    fun next(): T = move()

    fun move(step: Int = 1): T

    fun self(matcher: Matcher<T>): T

    fun selfPartial(matcher: Matcher<T>): T

    fun panic(rule: Any): Nothing
}