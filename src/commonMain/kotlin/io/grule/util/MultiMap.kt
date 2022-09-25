package io.grule.util

typealias MultiMap<K, V> = Map<K, List<V>>

fun <K, V> MultiMap<K, V>.get(key: K, index: Int): V? {
    return get(key)?.get(index)
}

fun <K, V> MultiMap<K, V>.getOrEmpty(key: K): List<V> {
    return get(key) ?: emptyList()
}

fun <K, V> MultiMap<K, V>.first(key: K): V = firstOrNull(key)!!

fun <K, V> MultiMap<K, V>.firstOrNull(key: K): V? = get(key)?.firstOrNull()

fun <K, V> MultiMap<K, V>.last(key: K): V = lastOrNull(key)!!

fun <K, V> MultiMap<K, V>.lastOrNull(key: K): V? = get(key)?.lastOrNull()

