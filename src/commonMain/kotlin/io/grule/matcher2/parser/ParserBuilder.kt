package io.grule.matcher2.parser

import io.grule.lexer.Lexer

class ParserBuilder {
    operator fun plus(text: String): ParserMatcher {
        return ParserMatcherString(text)
    }

    operator fun plus(lexer: Lexer): ParserMatcher {
        return ParserMatcherLexer(lexer)
    }
}