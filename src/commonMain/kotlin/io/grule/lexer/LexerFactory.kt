package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.MatcherException
import io.grule.matcher.MatcherSupplier
import kotlin.properties.ReadOnlyProperty

class LexerFactory internal constructor() : Lexer {
    private val lexers = mutableListOf<Lexer>()

    override fun lex(context: LexerContext) {
        var matches = false
        for (item in lexers) {
            try {
                item.lex(context)
                matches = true
                break
            } catch (_: MatcherException) {
            }
        }
        if (!matches) {
            if (context.peek(0) == null) {
                context.emitEOF()
                return
            }
            throw MatcherException(context)
        }
    }

    operator fun invoke(fn: MatcherSupplier): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty(fn).also { lexers.add(it) }
    }

    operator fun invoke(): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty { X }
    }

    fun token(fn: MatcherSupplier): Lexer {
        return add(Lexer.of(fn(Matcher), true))
    }

    fun skip(fn: MatcherSupplier): Lexer {
        return add(Lexer.of(fn(Matcher), false))
    }

    fun add(lexer: Lexer): Lexer {
        lexers.add(lexer)
        return lexer
    }
}