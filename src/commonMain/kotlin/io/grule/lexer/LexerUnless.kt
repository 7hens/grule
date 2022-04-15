package io.grule.lexer

internal class LexerUnless(val lexer: Lexer, val terminal: Lexer) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        var result = 0
        try {
            result += lexer.match(charStream, offset + result)
            if (result > 0) {
                result += match(charStream, offset + result)
            } else {
                throw LexerException("lexer ($lexer) matched nothing until ($terminal)")
            }
        } catch (_: LexerException) {
            result += terminal.match(charStream, offset + result)
        }
        return result
    }

    override fun toString(): String {
        return "$lexer *> $terminal"
    }
}