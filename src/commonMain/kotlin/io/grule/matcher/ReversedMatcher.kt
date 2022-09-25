package io.grule.matcher

interface ReversedMatcher<T : Status<T>> : Matcher<T> {
    val reverser: Matcher<T>
}