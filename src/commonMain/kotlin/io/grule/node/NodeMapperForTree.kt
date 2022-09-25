package io.grule.node

internal class NodeMapperForTree(val consumer: (AstNode) -> Unit) : NodeMapper {

    override fun apply(node: AstNode): AstNode {
        consumer(node)
        node.children.forEach { apply(it) }
        return node
    }
}