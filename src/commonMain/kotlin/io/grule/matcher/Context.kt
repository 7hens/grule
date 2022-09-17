package io.grule.matcher

interface Context {
    fun <V> prop(key: String): Prop<V>
}