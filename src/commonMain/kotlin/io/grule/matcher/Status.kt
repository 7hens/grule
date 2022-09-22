package io.grule.matcher

import io.grule.node.KeyOwner

interface Status<T : Status<T>> : KeyOwner {

    fun withKey(key: Any): T

    fun next(): T

    fun self(): T

    fun self(matcher: Matcher<T>): T

    fun panic(rule: Any): Nothing
}