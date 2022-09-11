package io.grule.parser

import io.grule.token.TokenStream
import io.grule.node.AstNode

internal class ParserUntilGreedy(
    val parser: Parser, val terminal: Parser, val minTimes: Int, val maxTimes: Int
) : Parser {
    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        var lastResult = 0
        val parserNode = AstNode.of(parentNode)
        while (true) {
            try {
                lastResult = result
                result += parser.parse(tokenStream, parserNode, offset + result)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    throw ParserException("limit out of range $maxTimes")
                }
            } catch (parserException: ParserException) {
                require(repeatTimes >= minTimes, parserException)
                try {
                    val terminalNode = AstNode.of(parentNode)
                    result += terminal.parse(tokenStream, terminalNode, offset + result)
                    parentNode.merge(parserNode)
                    parentNode.merge(terminalNode)
                } catch (terminalException: ParserException) {
                    require(repeatTimes - 1 >= minTimes, parserException)
                    val terminalNode = AstNode.of(parentNode)
                    result = lastResult + terminal.parse(tokenStream, terminalNode, offset + lastResult)
                    parentNode.merge(parserNode)
                    parentNode.merge(terminalNode)
                }
                return result
            }
        }
    }

    private fun require(condition: Boolean, exception: ParserException) {
        if (!condition) {
            throw exception
        }
    }

    override fun toString(): String {
        val maxText = if (maxTimes == Int.MAX_VALUE) "$maxTimes" else ""
        return "{$parser|$minTimes,$maxText*$terminal}"
    }
}