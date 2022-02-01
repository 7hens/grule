package io.grule.lexer

internal class LexerRepeat(val lexer: Lexer, val minTimes: Int, val maxTimes: Int) : Lexer() {

    override fun match(charStream: CharStream, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            try {
                result += lexer.match(charStream, offset + result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    return result
                }
            } catch (e: Throwable) {
                if (repeatTimes < minTimes) {
                    throw e
                }
                return result
            }
        }
    }

    override fun toString(): String {
        return "$lexer *"
    }
}