package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.node.AstNode
import io.grule.token.CharStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ParserProperty(
    val lexer: Lexer,
    fn: () -> ParserMatcher,
) : Parser, ReadOnlyProperty<Any?, Parser> {
    
    private val matcher: ParserMatcher by lazy(fn)

    private var name: String? = null

    override val key: Any get() = this

    override fun getValue(thisRef: Any?, property: KProperty<*>): Parser {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name ?: matcher.toString()
    }

    override fun match(status: ParserStatus): ParserStatus {
        val result = matcher.match(status.withNode(newNode()))
        return result.withNode(status.node + result.node.trimSingle())
    }

    override fun matchesEmpty(): Boolean {
        return matcher.matchesEmpty()
    }

    override fun parse(source: CharStream): AstNode {
        return parse(lexer.tokenStream(source))
    }
}