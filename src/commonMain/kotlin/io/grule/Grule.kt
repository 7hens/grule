package io.grule

import io.grule.matcher.*
import io.grule.parser.Parser
import io.grule.parser.ParserRecurse
import io.grule.lexer.Lexer
import io.grule.lexer.LexerContext
import io.grule.lexer.LexerRules
import io.grule.lexer.Lexers

@Suppress("PropertyName", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
open class Grule : Lexer() {
    private val rules = LexerRules()

    fun p(fn: (Parser) -> Parser): Parser {
        return ParserRecurse(fn)
    }

    private var lexers = mutableListOf<Lexer>(rules)

    override fun lex(context: LexerContext) {
        lexers.last().lex(context)
    }

    fun push(lexer: Lexer) {
        lexers.add(lexer)
    }

    fun pop() {
        lexers.removeLast()
    }

    fun addRule(lexer: Lexer) {
        rules.add(lexer)
    }

    fun addIndentRules(newLine: Lexer, indent: Lexer, dedent: Lexer) {
        rules.addIndentRules(newLine, indent, dedent)
    }

    fun token(matcher: Matcher): Lexer {
        return Lexers.token(matcher).also { addRule(it) }
    }

    fun token(fn: Matcher.Companion.() -> Matcher): Lexer {
        return token(fn(Matcher))
    }

    fun skip(matcher: Matcher): Lexer {
        return Lexers.skip(matcher).also { addRule(it) }
    }

    fun skip(fn: Matcher.Companion.() -> Matcher): Lexer {
        return skip(fn(Matcher))
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grule.() -> T): T {
            return fn(Grule())
        }
    }
}

