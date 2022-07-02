package io.grule.parser

import io.grule.lexer.TokenStream

/**
 *
 */
internal class ParserBinary(
    val parser: Parser,
    val operator: Any,
    val comparator: Comparator<AstNode>,
) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val parentKey = parentNode.key
        val nodeTree = AstNode(parentKey)
        val result = parser.parse(tokenStream, nodeTree, offset)
        if (nodeTree.all().isEmpty()) {
            return result
        }

        var rightNode = AstNode(parentKey)
        var leftNode = rightNode
        var opNode = AstNode(operator)
        var hasOp = false
        var isOpNode = false
        nodeTree.all().forEach { node ->
            isOpNode = node.key == operator
            if (isOpNode) {
                if (hasOp) {
                    leftNode = mergeNode(leftNode, opNode, rightNode)
                }
                hasOp = true
                opNode = node
                rightNode = AstNode(parentKey)
            } else {
                rightNode.add(node)
            }
        }
        if (!isOpNode) {
            leftNode = mergeNode(leftNode, opNode, rightNode)
        }
        parentNode.add(leftNode)
        if (isOpNode) {
            parentNode.add(opNode)
        }
        return result
    }

    private fun mergeNode(leftNode: AstNode, opNode: AstNode, rightNode: AstNode): AstNode {
        if (leftNode === rightNode) {
            return leftNode
        }
        val key = leftNode.key
        if (key !in leftNode) {
            val parentNode = AstNode(key)
            parentNode.add(leftNode)
            parentNode.add(opNode)
            parentNode.add(rightNode)
            return parentNode
        }
        val prevOpNode = leftNode.first(opNode.key)
        if (comparator.compare(prevOpNode, opNode) >= 0) {
            val node = AstNode(key)
            node.add(leftNode)
            node.add(opNode)
            node.add(rightNode)
            return node
        }

        val prevRightNode = leftNode.last(key)
        leftNode.remove(prevRightNode)
        leftNode.add(mergeNode(prevRightNode, opNode, rightNode))
        return leftNode
    }

    override fun toString(): String {
        return "(@ $parser, $operator)"
    }
}