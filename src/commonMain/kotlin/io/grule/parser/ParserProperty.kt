package io.grule.parser

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal abstract class ParserProperty : Parser, ReadOnlyProperty<Any?, Parser> {
    abstract val matcher: ParserMatcher
    private var name: String? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name ?: matcher.toString()
    }

    override fun match(status: ParserStatus): ParserStatus {
        val result = matcher.match(status.withNode(newNode()).withKey(key))
        return result.withNode(status.node + result.node)
    }

    override val key: Any get() = this

    companion object {
        operator fun invoke(fn: () -> ParserMatcher): ParserProperty {
            return object : ParserProperty() {
                override val matcher: ParserMatcher by lazy(fn)
            }
        }
    }
}