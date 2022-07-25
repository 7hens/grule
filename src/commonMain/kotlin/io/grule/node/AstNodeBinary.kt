package io.grule.node

/**
 * | Cases             | Examples            | Result              |
 * | ----------------- | ------------------- | ------------------- |
 * | Normal            | `1 + 2 * 3 - 4`     | `(1 + (2 * 3)) - 4` |
 * | Missing operators | `1   2   3 - 4`     | `(1    2   3)  - 4` |
 * | Missing elements  | `  + 2 *   - 4`     | `(@ + (2 * @)) - 4` |
 * | Structured tree   | `(1 + 2) * (3 - 4)` | `(1 + (2 * 3)) - 4` |
 */
internal class AstNodeBinary(
    val isOperator: (AstNode) -> Boolean,
    val comparator: Comparator<AstNode>
) : AstNode.Transformation {

    override fun apply(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return node
        }
        val opIndex = node.all().indexOfLast(isOperator)
        if (opIndex == -1) {
            if (node.size == 1 && node.contains(node.key)) {
                return apply(node.first())
            }
            return node.map { if (it.key == node.key) apply(it) else it }
        }
        val left = apply(node.subList(0, opIndex))
        val op = node[opIndex]
        val right = apply(node.subList(opIndex + 1))
        return sort(left, op, right)
    }

    private fun sort(left: AstNode, op: AstNode, right: AstNode): AstNode {
        if (right.all().any(isOperator)) {
            val (rightLeft, rightOp, rightRight) = right.all()
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
        val leftTail = left.last(left.key)
        left.remove(leftTail)
        left.add(sortLeft(leftTail, op, right))
        return left
    }

    private fun isPrior(element: AstNode, op: AstNode): Boolean {
        val opKey = op.key
        if (opKey !in element) {
            return true
        }
        val prevOpNode = element.first(opKey)
        return comparator.compare(prevOpNode, op) >= 0
    }

    private fun exp(left: AstNode, op: AstNode, right: AstNode): AstNode {
        return AstNode.of(left.key, left, op, right)
    }
}