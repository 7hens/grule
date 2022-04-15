package io.grule.lexer

internal class LexerUntil(val lexer: Lexer, val terminal: Lexer) : Lexer() {
    override fun match(charStream: CharStream, offset: Int): Int {
        var result = 0
        try {
            result += terminal.match(charStream, offset + result)
        } catch (_: LexerException) {
            result += lexer.match(charStream, offset + result)
            if (result > 0) {
                result += match(charStream, offset + result)
            } else {
                throw LexerException("lexer ($lexer) matched nothing until ($terminal)")
            }
        }
        return result
    }

    override fun toString(): String {
        return "$lexer *?> $terminal"
    }
}