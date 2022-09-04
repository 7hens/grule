package io.grule.matcher2.parser

import io.grule.lexer.Lexer

object ParserDsl {
    val X = Builder()

    operator fun <T> invoke(fn: ParserDsl.() -> T): T = run(fn)

    class Builder {
        operator fun plus(matcher: ParserMatcher): ParserMatcher {
            return matcher
        }

        operator fun plus(text: String): ParserMatcher {
            return plus(ParserMatcherString(text))
        }

        operator fun plus(lexer: Lexer): ParserMatcher {
            return plus(ParserMatcherLexer(lexer))
        }
    }
}