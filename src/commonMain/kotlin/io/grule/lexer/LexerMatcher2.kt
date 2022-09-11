package io.grule.lexer

import io.grule.matcher2.lexer.LexerMatcher
import io.grule.matcher2.lexer.LexerMatcherStatus

internal class LexerMatcher2(private val matcher: LexerMatcher, val emitsToken: Boolean) : Lexer {
    private var lexer: Lexer = this

    override fun lex(context: LexerContext) {
        val status = LexerMatcherStatus.from(context).apply(matcher)
        val matchNum = status.position
        if (emitsToken) {
            context.emit(lexer, matchNum)
        }
        context.moveNext(matchNum)
    }

    override fun toString(): String {
        return matcher.toString()
    }
}