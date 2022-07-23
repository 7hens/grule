package io.grule.node

internal class AstNodeFlat(private val predicate: AstNode.Predicate) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        flatNode(node)
        return node
    }

    private fun flatNode(node: AstNode) {
        if (node.isEmpty()) {
            return
        }
        val resultNode = AstNode(node.key)
        node.all().forEach { child ->
            flatNode(child)
            if (predicate.test(child)) {
                resultNode.merge(child)
            } else {
                resultNode.add(child)
            }
        }
        node.clear()
        node.merge(resultNode)
    }
}