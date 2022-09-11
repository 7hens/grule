package io.grule.lexer

object LexerEOF : Lexer {
    override fun lex(context: LexerContext) {
        LexerMatcherDsl.EOF.match(LexerMatcherStatus.from(context))
        context.emit(this)
    }

    override fun toString(): String {
        return LexerMatcherDsl.EOF.toString()
    }
}
