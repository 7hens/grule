package io.grule.parser

import io.grule.node.AstNode
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

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val nodeChain = status.nodeChainProp.get()!!
        val parentMatcher = status.parentMatcher.get()
        val isolatedNode = AstNode.of(this)
        try {
            status.parentMatcher.set(this)
            val result = status.withNode(isolatedNode).apply(matcher)
            status.node.add(isolatedNode)
            status.parentMatcher.set(parentMatcher)
            return result.withNode(status.node)
        } finally {
            status.nodeChainProp.set(nodeChain)
        }
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