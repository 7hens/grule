package io.grule.lexer

internal object LexerEOF : Lexer {
    override fun lex(context: LexerContext) {
        LexerDsl.EOF.match(context)
        context.emit(this)
    }

    override fun toString(): String {
        return LexerDsl.EOF.toString()
    }
}
