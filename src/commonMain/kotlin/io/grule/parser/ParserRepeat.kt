package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserRepeat(val parser: Parser, val minTimes: Int, val maxTimes: Int) : Parser() {
    init {
        require(minTimes >= 0)
        require(maxTimes >= minTimes)
    }

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        var repeatTimes = 0
        var result = 0
        val node = AstNode(parentNode.key)
        while (true) {
            try {
                result += parser.parse(tokenStream, node, offset + result)
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
        parentNode.merge(node)
        return result
    }

    override fun isRecursive(parser: Parser): Boolean {
        return this === parser || parser.isRecursive(parser)
    }
}