package io.grule.node

/**
 * | Cases             | Examples            | Result              |
 * | ----------------- | ------------------- | ------------------- |
 * | Normal            | `1 + 2 * 3 - 4`     | `(1 + (2 * 3)) - 4` |
 * | Missing operators | `1   2   3 - 4`     | `(1    2   3)  - 4` |
 * | Missing elements  | `  + 2 *   - 4`     | `(_ + (2 * _)) - 4` |
 * | Structured tree   | `(1 + 2) * (3 - 4)` | `(1 + (2 * 3)) - 4` |
 */
internal class AstNodeBinary(
    val isOperator: (AstNode) -> Boolean,
    val comparator: Comparator<AstNode>
) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return node
        }
        val operatorIndex = node.all().indexOfLast(isOperator)
        if (operatorIndex == -1) {
            if (node.size == 1 && node.contains(node.key)) {
                return map(node.first())
            }
            return node.map { if (it.key == node.key) map(it) else it }
        }
        val leftNode = node.subList(0, operatorIndex)
        val opNode = node.all()[operatorIndex]
        val rightNode = node.subList(operatorIndex + 1)
        return mergeNode(map(leftNode), opNode, map(rightNode))
    }

    private fun mergeNode(leftNode: AstNode, opNode: AstNode, rightNode: AstNode): AstNode {
        if (leftNode === rightNode) {
            return leftNode
        }
        require(leftNode.key == rightNode.key)

        val parentKey = leftNode.key
        if (isLeftPriorityGreater(leftNode, opNode)) {
            return AstNode.of(parentKey, leftNode, opNode, rightNode)
        }
        val prevRightNode = leftNode.last(parentKey)
        leftNode.remove(prevRightNode)
        leftNode.add(mergeNode(prevRightNode, opNode, rightNode))
        return leftNode
    }

    private fun isLeftPriorityGreater(leftNode: AstNode, opNode: AstNode): Boolean {
        val opKey = opNode.key
        if (opKey !in leftNode) {
            return true
        }
        val prevOpNode = leftNode.first(opKey)
        return comparator.compare(prevOpNode, opNode) >= 0
    }
}