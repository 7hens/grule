package io.grule.lexer

internal object LexerEof : Lexer {

    override fun lex(context: LexerContext) {
        LexerMatcherEof.match(context)
        context.emit(this)
    }

    override fun toString(): String {
        return LexerMatcherEof.toString()
    }

}
