package io.grule.lexer

internal class LexerImpl(private val matcher: LexerMatcher, val emitsToken: Boolean) : Lexer {
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