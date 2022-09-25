package io.grule.matcher

interface Status<T : Status<T>> {

    fun next(): T

    fun self(matcher: Matcher<T>): T

    fun selfPartial(matcher: Matcher<T>): T

    fun panic(rule: Any): Nothing
}