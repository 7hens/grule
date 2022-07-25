package io.grule.node

interface KeyProvider {
    val key: Any

    fun keyEquals(other: Any): Boolean {
        return key == keyOf(other)
    }

    companion object {
        fun keyOf(value: Any): Any {
            return (value as? KeyProvider)?.key ?: value
        }
    }
}