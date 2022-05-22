package io.grule.parser

import io.grule.lexer.TokenStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ParserProperty(fn: () -> Parser) : Parser, ReadOnlyProperty<Any?, Parser> {
    private var name: String? = null
    private val parser by lazy(fn)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name ?: parser.toString()
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode(this)
        val result = parser.parse(tokenStream, node, offset)
        parentNode.add(node)
        return result
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || (name == null && parser.isRecursive(parser))
    }
}