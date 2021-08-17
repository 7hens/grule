package io.grule.lexer

internal class LexerNot(private val lexer: Lexer) : Lexer() {

    override fun match(input: CharStream, offset: Int): Int {
        val c = input.peek(offset)
        if (c == CharStream.EOF) {
            throw LexerException("expected ${lexer}, actual is <EOF>")
        }
        try {
            lexer.match(input, offset)
        } catch (e: Throwable) {
            return 1
        }
        throw LexerException()
    }

    override fun not(): Lexer {
        return lexer
    }
}