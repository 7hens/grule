package io.grule.matcher

interface Status<T : Status<T>> : ContextOwner {
    fun apply(matcher: Matcher<T>): T

    fun next(): T

    fun self(): T

    fun panic(rule: Any): Nothing

    val lastMatcher: Prop<Matcher<T>> get() = prop("lastMatcher")

    val parentMatcher: Prop<Matcher<T>> get() = prop("parentMatcher")
}