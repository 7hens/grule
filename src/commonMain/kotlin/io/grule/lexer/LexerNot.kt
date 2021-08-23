package io.grule.lexer

internal class LexerNot(private val lexer: Lexer) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        val c = charStream.peek(offset)
        if (c == CharStream.EOF) {
            throw LexerException("expected ${lexer}, actual is <EOF>")
        }
        try {
            lexer.match(charStream, offset)
        } catch (e: Throwable) {
            return 1
        }
        throw LexerException()
    }

    override fun not(): Lexer {
        return lexer
    }
}