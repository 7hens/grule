package io.grule.lexer

internal class LexerImpl(private val matcher: LexerMatcher, val emitsToken: Boolean) : Lexer {
    private var lexer: Lexer = this

    override fun lex(context: LexerContext) {
        val matchNum = matcher.match(context)
        if (emitsToken) {
            context.emit(lexer, matchNum)
        } else {
            context.moveNext(matchNum)
        }
    }

    override fun toString(): String {
        return matcher.toString()
    }
}