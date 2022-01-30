package io.grule.parser

internal abstract class ParserBinary(
    val opParser: Parser,
    val leftParser: Parser,
    val rightParser: Parser,
    private val comparator: Comparator<AstNode>
) : Parser() {

    fun mergeNode(opNode: AstNode, leftNode: AstNode, rightNode: AstNode): AstNode {
        val key = opNode.key
        if (key !in leftNode) {
            val parentNode = AstNode(key)
            parentNode.add(leftNode)
            parentNode.add(opNode)
            parentNode.add(rightNode)
            return parentNode
        }
        val prevOpNode = leftNode.all(key)[1]
        if (comparator.compare(prevOpNode, opNode) >= 0) {
            val node = AstNode(key)
            node.add(leftNode)
            node.add(opNode)
            node.add(rightNode)
            return node
        }

        val prevRightNode = leftNode.last(key)
        leftNode.remove(prevRightNode)
        leftNode.add(mergeNode(opNode, prevRightNode, rightNode))
        return leftNode
    }
}