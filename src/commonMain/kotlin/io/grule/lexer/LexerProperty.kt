package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.MatcherContext
import io.grule.matcher.MatcherFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class LexerProperty(fn: MatcherFactory) : Lexer, ReadOnlyProperty<Any?, Lexer> {
    private var name: String? = null
    private val lexer by lazy { Lexer.of(fn(Matcher), name != null) }
    private val matcher by lazy { fn(Matcher) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Lexer {
        name = property.name
        return this
    }

    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        if (name != null) {
            context.emit(this, matchNum)
        }
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return name ?: lexer.toString()
    }
}