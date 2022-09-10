package io.grule.lexer

import io.grule.matcher2.MatcherException
import io.grule.matcher2.lexer.LexerMatcher
import io.grule.matcher2.lexer.LexerMatcherDsl
import kotlin.properties.ReadOnlyProperty

class LexerFactory2 internal constructor() : Lexer {
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

    fun m2(fn: LexerMatcherDsl.() -> LexerMatcher): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty2 { fn(LexerMatcherDsl) }.also { lexers.add(it) }
    }

    operator fun invoke(fn: LexerMatcherDsl.() -> LexerMatcher): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty2 { fn(LexerMatcherDsl) }.also { lexers.add(it) }
    }

    operator fun invoke(): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty { X }
    }

    fun token(fn: LexerMatcherDsl.() -> LexerMatcher): Lexer {
        return add(LexerMatcher2(fn(LexerMatcherDsl), true))
    }

    fun skip(fn: LexerMatcherDsl.() -> LexerMatcher): Lexer {
        return add(LexerMatcher2(fn(LexerMatcherDsl), false))
    }

    fun add(lexer: Lexer): Lexer {
        lexers.add(lexer)
        return lexer
    }
}