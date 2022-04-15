package io.grule.lexer

/**
 * non-greedy: a*?b
 */
internal class LexerUntilNonGreedy(
    val lexer: Lexer, val terminal: Lexer, val minTimes: Int, val maxTimes: Int) : Lexer() {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(charStream: CharStream, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            if (repeatTimes >= minTimes) {
                try {
                    result += terminal.match(charStream, offset + result)
                    return result
                } catch (_: LexerException) {
                }
            }
            result += lexer.match(charStream, offset + result)
            repeatTimes++
            if (repeatTimes > maxTimes) {
                throw LexerException("limit out of range $maxTimes")
            }
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$lexer|$minTimes,$maxText*?$terminal}"
    }
}