package io.grule.lexer

import io.grule.matcher.MatcherException
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
                context.emitEof()
                return
            }
            throw MatcherException("Unmatched item with [ " + lexers.joinToString(" | ") + " ] at ${context.position}")
        }
    }

    operator fun invoke(fn: LexerSupplier): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty { fn(LexerDsl).cache() }.also { lexers.add(it) }
    }

    operator fun invoke(): ReadOnlyProperty<Any?, Lexer> {
        return invoke { ANY.not() }
    }

    fun token(fn: LexerSupplier): Lexer {
        return add(LexerImpl(fn(LexerDsl), true))
    }

    fun skip(fn: LexerSupplier): Lexer {
        return add(LexerImpl(fn(LexerDsl), false))
    }

    fun add(lexer: Lexer): Lexer {
        lexers.add(lexer)
        return lexer
    }
}