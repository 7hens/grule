package io.grule.matcher

interface Prop<T> {
    val key: String
    fun get(): T?
    fun set(value: T?)
    fun set(fn: (T) -> T) {
        set(fn(get()!!))
    }
}