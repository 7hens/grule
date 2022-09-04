package io.grule.node

interface KeyProvider {
    val key: Any

    fun keyEquals(other: Any): Boolean {
        return key == keyOf(other)
    }

    companion object {
        fun keyOf(value: Any): Any {
            return invoke(value).key
        }

        operator fun invoke(value: Any): KeyProvider {
            return (value as? KeyProvider) ?: Impl(value)
        }
    }

    private class Impl(override val key: Any) : KeyProvider
}