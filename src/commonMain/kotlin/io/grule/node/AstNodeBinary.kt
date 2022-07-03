package io.grule.node

internal class AstNodeBinary(val isOperator: AstNode.Predicate, val comparator: Comparator<AstNode>) : AstNode.Mapper {
    override fun map(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return node
        }
        val parentKey = node.key
        val parentNode = AstNode(parentKey)
        var rightNode = AstNode(parentKey)
        var leftNode = rightNode
        lateinit var opNode: AstNode
        var hasOp = false
        var isOpNode = false
        node.all().forEach { child ->
            isOpNode = isOperator.test(child)
            if (isOpNode) {
                if (hasOp) {
                    leftNode = mergeNode(leftNode, opNode, rightNode)
                }
                hasOp = true
                opNode = child
                rightNode = AstNode(parentKey)
            } else {
                rightNode.add(child)
            }
        }
        if (isOpNode) {
            parentNode.add(leftNode)
            parentNode.add(opNode)
        } else {
            leftNode = mergeNode(leftNode, opNode, rightNode)
            parentNode.merge(leftNode)
        }
        return parentNode
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
}