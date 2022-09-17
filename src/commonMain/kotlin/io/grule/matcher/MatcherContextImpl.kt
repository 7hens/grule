package io.grule.matcher

internal class MatcherContextImpl : Context {
    private val map = mutableMapOf<String, Any?>()

    override fun <V> prop(key: String): Prop<V> {
        return PropImpl(key)
    }

    private inner class PropImpl<V>(override val key: String) : Prop<V> {

        @Suppress("UNCHECKED_CAST")
        override fun get(): V? {
            return map[key] as? V
        }

        override fun set(value: V?) {
            map[key] = value as? Any
        }
    }
}