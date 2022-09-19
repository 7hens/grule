package io.grule.node

import io.grule.util.first

/**
 * | Cases             | Examples            | Result              |
 * | ----------------- | ------------------- | ------------------- |
 * | Normal            | `1 + 2 * 3 - 4`     | `(1 + (2 * 3)) - 4` |
 * | Missing operators | `1   2   3 - 4`     | `(1    2   3)  - 4` |
 * | Missing elements  | `  + 2 *   - 4`     | `(@ + (2 * @)) - 4` |
 * | Structured tree   | `(1 + 2) * (3 - 4)` | `(1 + (2 * 3)) - 4` |
 */
internal class NodeMapperBinary(
    val isOperator: (AstNode) -> Boolean,
    val comparator: Comparator<AstNode>
) : NodeMapper {

    override fun apply(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return node
        }
        val opIndex = node.children.indexOfLast(isOperator)
        if (opIndex == -1) {
            if (node.size == 1 && node.map.containsKey(node.key)) {
                return apply(node.children.first())
            }
            return node.map { if (it.key == node.key) apply(it) else it }
        }
        val left = apply(node.subList(0, opIndex))
        val op = node.children[opIndex]
        val right = apply(node.subList(opIndex + 1))
        return sort(left, op, right)
    }

    private fun sort(left: AstNode, op: AstNode, right: AstNode): AstNode {
        if (right.children.any(isOperator)) {
            val (rightLeft, rightOp, rightRight) = right.children
            return sort(exp(left, op, rightLeft), rightOp, rightRight)
        }
        return sortLeft(left, op, right)
    }

    private fun sortLeft(left: AstNode, op: AstNode, right: AstNode): AstNode {
        if (left === right) {
            return left
        }
        require(left.key == right.key)

        if (isPrior(left, op)) {
            return exp(left, op, right)
        }
        val leftTail = left.children.last()
        val leftBody = left.subList(0, left.size - 1)
        return leftBody + sortLeft(leftTail, op, right)
    }

    private fun isPrior(element: AstNode, op: AstNode): Boolean {
        val opKey = op.key
        if (opKey !in element.map) {
            return true
        }
        val prevOpNode = element.map.first(opKey)
        return comparator.compare(prevOpNode, op) >= 0
    }

    private fun exp(left: AstNode, op: AstNode, right: AstNode): AstNode {
        return left.newNode(left, op, right)
    }
}