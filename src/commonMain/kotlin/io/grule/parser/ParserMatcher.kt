package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Matcher

typealias ParserMatcher = Matcher<ParserMatcherStatus>

typealias ParserSupplier = ParserDsl.() -> ParserMatcher

interface ParserMatcherExt {

    operator fun ParserMatcher.plus(text: String): ParserMatcher {
        return plus(ParserMatcherString(text))
    }

    operator fun ParserMatcher.plus(lexer: Lexer): ParserMatcher {
        return plus(ParserMatcherLexer(lexer))
    }
}