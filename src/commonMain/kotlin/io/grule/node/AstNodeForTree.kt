package io.grule.node

internal class AstNodeForTree(val consumer: (AstNode) -> Unit) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        consumer(node)
        node.all().forEach { map(it) }
        return node
    }
}