package io.grule.lexer

internal class LexerUntil(
    private val lexer: Lexer,
    private val minTimes: Int,
    private val terminal: Lexer) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            try {
                result += lexer.match(charStream, offset + result)
                repeatTimes++
            } catch (e: Throwable) {
                if (repeatTimes < minTimes) {
                    throw e
                }
            }
            try {
                return result + terminal.match(charStream, offset + result)
            } catch (_: Throwable) {
            }
        }
    }

    override fun toString(): String {
        return "$lexer *> $terminal"
    }
}