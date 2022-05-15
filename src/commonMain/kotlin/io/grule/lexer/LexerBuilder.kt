package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.matcher.MatcherException
import io.grule.matcher.MatcherFactory
import kotlin.properties.ReadOnlyProperty

class LexerBuilder internal constructor() : Lexer {
    private val lexers = mutableListOf<Lexer>()

    override fun lex(context: LexerContext) {
        var matches = false
        for (lexer in lexers) {
            try {
                lexer.lex(context)
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

    operator fun invoke(fn: MatcherFactory): ReadOnlyProperty<Any?, Lexer> {
        return LexerProperty(fn).also { lexers.add(it) }
    }

    fun token(fn: MatcherFactory): Lexer {
        return add(Lexer.of(fn(Matcher), true))
    }

    fun skip(fn: MatcherFactory): Lexer {
        return add(Lexer.of(fn(Matcher), false))
    }

    fun add(lexer: Lexer): Lexer {
        lexers.add(lexer)
        return lexer
    }
}