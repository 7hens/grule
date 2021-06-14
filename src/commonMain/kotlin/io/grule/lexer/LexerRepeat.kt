package io.grule.lexer

internal class LexerRepeat(
    private val lexer: Lexer,
    private val minTimes: Int,
    private val maxTimes: Int) : Lexer() {

    override fun match(input: CharStream, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            try {
                result += lexer.match(input, offset + result)
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
}