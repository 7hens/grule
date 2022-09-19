package io.grule.node2

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
    val isOperator: (Node) -> Boolean,
    val comparator: Comparator<Node>
) : NodeMapper {

    override fun apply(node: Node): Node {
        if (node.isEmpty()) {
            return node
        }
        val opIndex = node.list.indexOfLast(isOperator)
        if (opIndex == -1) {
            if (node.size == 1 && node.map.containsKey(node.key)) {
                return apply(node.list.first())
            }
            return node.map { if (it.key == node.key) apply(it) else it }
        }
        val left = apply(node.subList(0, opIndex))
        val op = node.list[opIndex]
        val right = apply(node.subList(opIndex + 1))
        return sort(left, op, right)
    }

    private fun sort(left: Node, op: Node, right: Node): Node {
        if (right.list.any(isOperator)) {
            val (rightLeft, rightOp, rightRight) = right.list
            return sort(exp(left, op, rightLeft), rightOp, rightRight)
        }
        return sortLeft(left, op, right)
    }

    private fun sortLeft(left: Node, op: Node, right: Node): Node {
        if (left === right) {
            return left
        }
        require(left.key == right.key)

        if (isPrior(left, op)) {
            return exp(left, op, right)
        }
        val leftTail = left.list.last()
        val leftBody = left.subList(0, left.size - 1)
        return leftBody + sortLeft(leftTail, op, right)
    }

    private fun isPrior(element: Node, op: Node): Boolean {
        val opKey = op.key
        if (opKey !in element.map) {
            return true
        }
        val prevOpNode = element.map.first(opKey)
        return comparator.compare(prevOpNode, op) >= 0
    }

    private fun exp(left: Node, op: Node, right: Node): Node {
        return left.newNode(left, op, right)
    }
}