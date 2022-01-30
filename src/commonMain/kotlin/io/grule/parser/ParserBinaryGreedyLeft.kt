package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserBinaryGreedyLeft(
    opParser: Parser,
    leftParser: Parser,
    rightParser: Parser,
    comparator: Comparator<AstNode>
) : ParserBinary(opParser, leftParser, rightParser, comparator) {

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val key = parentNode.key
        var result = 0
        val opNode = AstNode(key)
        val leftNode = AstNode(key)
        try {
            result += parseLeftAndOp(tokenStream, offset + result, leftNode, opNode)
        } catch (e: Throwable) {
            return result + parseRight(tokenStream, offset + result, parentNode, leftNode, opNode)
        }
        return result + parseNext(tokenStream, offset + result, parentNode, leftNode, opNode)
    }

    private fun parseLeftAndOp(tokenStream: TokenStream, offset: Int, leftNode: AstNode, opNode: AstNode): Int {
        var result = 0
        result += leftParser.parse(tokenStream, offset + result, leftNode)
        result += opParser.parse(tokenStream, offset + result, opNode)
        return result
    }

    private fun parseRight(
        tokenStream: TokenStream,
        offset: Int,
        parentNode: AstNode,
        leftNode: AstNode,
        opNode: AstNode
    ): Int {
        val key = parentNode.key
        val rightNode = AstNode(key)
        val result = rightParser.parse(tokenStream, offset, rightNode)
        val finalNode = mergeNode(opNode, leftNode, rightNode)
        parentNode.merge(finalNode)
        return result
    }

    private fun parseNext(
        tokenStream: TokenStream,
        offset: Int,
        parentNode: AstNode,
        prevLeftNode: AstNode,
        prevOpNode: AstNode
    ): Int {
        val key = prevLeftNode.key
        var lastLeftNode = prevLeftNode
        var lastOpNode = prevOpNode
        var result = 0
        try {
            val leftNode = AstNode(key)
            val opNode = AstNode(key)
            result += parseLeftAndOp(tokenStream, offset + result, leftNode, opNode)
            lastLeftNode = mergeNode(prevOpNode, prevLeftNode, leftNode)
            lastOpNode = opNode
            result += parseNext(tokenStream, offset + result, parentNode, lastLeftNode, lastOpNode)
        } catch (e: Throwable) {
            result += parseRight(tokenStream, offset + result, parentNode, lastLeftNode, lastOpNode)
        }
        return result
    }
}