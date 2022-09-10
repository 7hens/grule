package io.grule.matcher2.parser

import io.grule.node.AstNode
import io.grule.node.AstNodeChain
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal abstract class ParserProperty : Parser, ReadOnlyProperty<Any?, Parser> {
    abstract val matcher: ParserMatcher
    private lateinit var name: String

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val node = AstNode.of(this)
        val result = status.withNode { AstNodeChain.of(node) }.apply(matcher)
        status.nodeChain.me.add(node)
        return result.withNode { status.nodeChain }
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