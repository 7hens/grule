package io.grule.lexer

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal abstract class LexerProperty2 : Lexer, ReadOnlyProperty<Any?, Lexer> {
    private var name: String? = null
    abstract val matcher: LexerMatcher

    override fun getValue(thisRef: Any?, property: KProperty<*>): Lexer {
        name = property.name
        return this
    }

    override fun lex(context: LexerContext) {
        val status = LexerMatcherStatus.from(context).apply(matcher)
        val matchNum = status.position
        context.emit(this, matchNum)
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return name ?: super.toString()
    }

    companion object {
        operator fun invoke(fn: () -> LexerMatcher): LexerProperty2 {
            return object : LexerProperty2() {
                override val matcher: LexerMatcher by lazy(fn)
            }
        }
    }
}