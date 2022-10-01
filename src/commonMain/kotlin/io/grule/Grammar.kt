package io.grule

import io.grule.lexer.Lexer
import io.grule.lexer.LexerContext
import io.grule.parser.Parser

@Suppress("MemberVisibilityCanBePrivate")
open class Grammar : Lexer {
    val lexer = Lexer.factory()
    val parser = Parser.factory(lexer)

    override fun lex(context: LexerContext) {
        return lexer.lex(context)
    }

    companion object {
        inline operator fun <T> invoke(crossinline fn: Grammar.() -> T): T {
            return fn(Grammar())
        }
    }
}

