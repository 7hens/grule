package io.grule.matcher2.parser

import io.grule.lexer.Lexer
import io.grule.matcher2.Matcher

typealias ParserMatcher = Matcher<ParserMatcherContext>

operator fun ParserMatcher.plus(text: String): ParserMatcher {
    return plus(ParserMatcherString(text))
}

operator fun ParserMatcher.plus(lexer: Lexer): ParserMatcher {
    return plus(ParserMatcherLexer(lexer))
}
