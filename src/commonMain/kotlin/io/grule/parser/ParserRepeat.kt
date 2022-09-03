package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal class ParserRepeat(val parser: Parser, val minTimes: Int, val maxTimes: Int) : Parser {
    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        while (true) {
            try {
                result += parser.parse(tokenStream, parentNode, offset + result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    break
                }
            } catch (e: ParserException) {
                if (repeatTimes < minTimes) {
                    throw e
                }
                break
            }
        }
        return result
    }

    override fun toString(): String {
        val maxText = if (maxTimes != Int.MAX_VALUE) "$maxTimes" else ""
        return "{$parser |$minTimes,$maxText}"
    }
}