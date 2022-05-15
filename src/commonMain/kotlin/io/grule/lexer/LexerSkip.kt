package io.grule.lexer

import io.grule.matcher.Matcher

internal class LexerSkip(private val matcher: Matcher) : Lexer() {
    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return matcher.toString()
    }
}