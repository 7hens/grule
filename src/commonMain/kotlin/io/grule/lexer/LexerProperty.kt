package io.grule.lexer

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class LexerProperty(
    fn: () -> LexerMatcher,
) : Lexer, ReadOnlyProperty<Any?, Lexer> {

    private val matcher: LexerMatcher by lazy(fn)

    private var name: String? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): Lexer {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name ?: matcher.toString()
    }

    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        context.emit(this, matchNum)
    }
}