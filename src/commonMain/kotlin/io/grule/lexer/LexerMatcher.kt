package io.grule.lexer

import io.grule.matcher.Matcher

internal class LexerMatcher(private val matcher: Matcher, val emitsToken: Boolean) : Lexer {
    private var lexer: Lexer = this

    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        if (emitsToken) {
            context.emit(lexer, matchNum)
        }
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return matcher.toString()
    }
}