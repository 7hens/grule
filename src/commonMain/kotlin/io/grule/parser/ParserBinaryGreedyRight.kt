package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserBinaryGreedyRight(
    opParser: Parser,
    leftParser: Parser,
    rightParser: Parser,
    comparator: Comparator<AstNode>
) : ParserBinary(opParser, leftParser, rightParser, comparator) {

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val key = parentNode.key
        var leftNode = AstNode(key)
        var result = leftParser.parse(tokenStream, offset, leftNode)
        while (true) {
            val opNode = AstNode(key)
            val rightNode = AstNode(key)
            try {
                result += parseRight(tokenStream, offset + result, opNode, rightNode)
            } catch (e: Throwable) {
                break
            }
            leftNode = mergeNode(opNode, leftNode, rightNode)
        }
        parentNode.merge(leftNode)
        return result
    }

    private fun parseRight(tokenStream: TokenStream, offset: Int, opNode: AstNode, rightNode: AstNode): Int {
        var result = 0
        result += opParser.parse(tokenStream, offset + result, opNode)
        result += rightParser.parse(tokenStream, offset + result, rightNode)
        return result
    }
}