package io.grule.lexer

import io.grule.matcher.Matcher

internal class LexerMatcher(private val matcher: Matcher, val emitsToken: Boolean) : Lexer {
    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        if (emitsToken) {
            context.emit(this, matchNum)
        }
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return matcher.toString()
    }
}