package io.grule.matcher

import io.grule.node.KeyProvider

interface Status<T : Status<T>> : ContextOwner, KeyProvider {

    fun next(): T

    fun self(): T

    fun key(key: Any): T

    fun panic(rule: Any): Nothing
}