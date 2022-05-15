package io.grule.lexer

internal class LexerNot(private val lexer: Lexer) : Lexer {

    override fun match(context: LexerContext, offset: Int): Int {
        context.peek(offset)
            ?: throw LexerException(context, lexer, Lexer.EOF)
        try {
            lexer.match(context, offset)
        } catch (e: LexerException) {
            return 1
        }
        throw LexerException()
    }

    override fun not(): Lexer {
        return lexer
    }

    override fun toString(): String {
        return "(! $lexer)"
    }
}