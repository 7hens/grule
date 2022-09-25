package io.grule.node

internal class NodeMapperMapTree(val mapper: (AstNode) -> AstNode) : NodeMapper {

    override fun apply(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return mapper(node)
        }
        return node.newNode(node.children.map { apply(it) })
    }
}