package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserUntilNonGreedy(
    val parser: Parser, val terminal: Parser, val minTimes: Int, val maxTimes: Int
) : Parser {
    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        val parserNode = AstNode(parentNode.key)
        while (true) {
            if (repeatTimes >= minTimes) {
                try {
                    val terminalNode = AstNode(parentNode.key)
                    result += terminal.parse(tokenStream, terminalNode, offset + result)
                    parentNode.merge(parserNode)
                    parentNode.merge(terminalNode)
                    return result
                } catch (_: ParserException) {
                }
            }
            result += parser.parse(tokenStream, parserNode, offset + result)
            repeatTimes++
            if (repeatTimes > maxTimes) {
                throw ParserException("limit out of range $maxTimes")
            }
        }
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || parser.isRecursive(parser)
    }
}