package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Matcher

object ParserMatcherDsl {
    val X: ParserMatcher = Matcher.shadow()

    operator fun <T> invoke(fn: ParserMatcherDsl.() -> T): T = run(fn)

    operator fun ParserMatcher.plus(text: String): ParserMatcher {
        return plus(ParserMatcherString(text))
    }

    operator fun ParserMatcher.plus(lexer: Lexer): ParserMatcher {
        return plus(ParserMatcherLexer(lexer))
    }
}