package io.grule.lexer

internal class LexerUnless(val lexer: Lexer, val terminal: Lexer) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        var result = 0
        try {
            result += lexer.match(charStream, offset + result)
            result += match(charStream, offset + result)
        } catch (_: Throwable) {
            result += terminal.match(charStream, offset + result)
        }
        return result
    }

    override fun toString(): String {
        return "$lexer *> $terminal"
    }
}