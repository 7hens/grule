package io.grule.lexer

internal class LexerRepeat(val lexer: Lexer, val minTimes: Int, val maxTimes: Int) : Lexer() {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

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
            } catch (e: LexerException) {
                if (repeatTimes < minTimes) {
                    throw e
                }
                return result
            }
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$lexer|$minTimes,$maxText}"
    }
}