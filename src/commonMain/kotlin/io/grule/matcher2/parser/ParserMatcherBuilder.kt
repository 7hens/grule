package io.grule.matcher2.parser

import io.grule.lexer.Lexer
import io.grule.matcher2.MatcherBuilder

class ParserMatcherBuilder : MatcherBuilder<ParserMatcherContext, ParserMatcherBuilder>() {
    
    operator fun plus(text: String): ParserMatcherBuilder {
        return plus(ParserMatcherString(text))
    }

    operator fun plus(lexer: Lexer): ParserMatcherBuilder {
        return plus(ParserMatcherLexer(lexer))
    }
}