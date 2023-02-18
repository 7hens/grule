package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.LexerEof.newNode
import io.grule.matcher.Matcher
import io.grule.node.AstNode
import io.grule.token.TokenStream

typealias ParserMatcher = Matcher<ParserStatus>

typealias ParserSupplier = ParserDsl.() -> ParserMatcher

interface ParserMatcherExt {

    operator fun ParserMatcher.plus(text: String): ParserMatcher {
        return plus(ParserMatcherString(text))
    }

    operator fun ParserMatcher.plus(lexer: Lexer): ParserMatcher {
        return plus(ParserMatcherLexer(lexer))
    }

    infix fun ParserMatcher.or(text: String): ParserMatcher {
        return or(ParserMatcherString(text))
    }

    infix fun ParserMatcher.or(lexer: Lexer): ParserMatcher {
        return or(ParserMatcherLexer(lexer))
    }

    fun ParserMatcher.parse(tokenStream: TokenStream): AstNode {
        val mainParser = this + Lexer.EOF
        val status = ParserStatus(newNode(), tokenStream)
        return mainParser.match(status).node.first()
    }
}