package io.grule.lexer

internal class LexerNot(private val lexer: Lexer) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        charStream.peek(offset)
            ?: throw LexerException(charStream, lexer, EOF)
        try {
            lexer.match(charStream, offset)
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