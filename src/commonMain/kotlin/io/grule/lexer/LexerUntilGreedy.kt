package io.grule.lexer

/**
 * greedy: a*b
 */
internal class LexerUntilGreedy(
    val lexer: Lexer, val terminal: Lexer, val minTimes: Int, val maxTimes: Int) : Lexer {

    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun match(context: LexerContext, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        var lastResult = 0
        while (true) {
            try {
                lastResult = result
                result += lexer.match(context, offset + result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    throw LexerException()
                }
            } catch (lexerException: LexerException) {
                require(repeatTimes >= minTimes, lexerException)
                try {
                    result += terminal.match(context, offset + result)
                } catch (terminalError: LexerException) {
                    require(repeatTimes - 1 >= minTimes, terminalError)
                    result = lastResult + terminal.match(context, offset + lastResult)
                }
                return result
            }
        }
    }

    private fun require(condition: Boolean, exception: LexerException) {
        if (!condition) {
            throw exception
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$lexer|$minTimes,$maxText*$terminal}"
    }
}