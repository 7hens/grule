package io.grule.matcher

interface ContextOwner : Context {
    val context: Context

    override fun <V> prop(key: String): Prop<V> = context.prop(key)
}