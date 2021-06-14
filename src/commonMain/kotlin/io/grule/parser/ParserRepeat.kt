package io.grule.parser

import io.grule.lexer.TokenChannel

internal class ParserRepeat(val parser: Parser, val minTimes: Int, val maxTimes: Int) : Parser() {
    override val isFlatten get() = true

    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        var repeatTimes = 0
        var result = 0
        val node = AstNode(this)
        while (true) {
            try {
                result += parser.tryParse(channel, offset + result, node)
                repeatTimes++
                if (repeatTimes == maxTimes) {
                    break
                }
            } catch (e: Throwable) {
                if (repeatTimes < minTimes) {
                    throw e
                }
                break
            }
        }
        parentNode.merge(node)
        return result
    }
}