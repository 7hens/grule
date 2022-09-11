package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Matcher

object ParserDsl {
    val X: ParserMatcher = Matcher.shadow()

    operator fun <T> invoke(fn: ParserDsl.() -> T): T = run(fn)

    operator fun ParserMatcher.plus(text: String): ParserMatcher {
        return plus(ParserMatcherString(text))
    }

    operator fun ParserMatcher.plus(lexer: Lexer): ParserMatcher {
        return plus(ParserMatcherLexer(lexer))
    }
}