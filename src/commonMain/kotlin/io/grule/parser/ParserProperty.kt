package io.grule.parser

import io.grule.token.TokenStream
import io.grule.node.AstNode
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal abstract class ParserProperty : Parser, ReadOnlyProperty<Any?, Parser> {
    private lateinit var name: String
    private val lazyParser by lazy { getParser() }

    abstract fun getParser(): Parser

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode.of(this)
        val result = lazyParser.parse(tokenStream, node, offset)
        parentNode.add(node)
        return result
    }
}