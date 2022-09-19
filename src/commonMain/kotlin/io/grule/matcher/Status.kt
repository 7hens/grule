package io.grule.matcher

import io.grule.node2.KeyOwner

interface Status<T : Status<T>> : ContextOwner, KeyOwner {

    fun next(): T

    fun self(): T

    fun key(key: Any): T

    fun panic(rule: Any): Nothing
}