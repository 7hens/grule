package io.grule.node

internal class NodeMapperTrimSingle : NodeMapper {

    override fun apply(node: AstNode): AstNode {
        if (!node.isSingle()) {
            return node
        }
        val singleChild = node.first()
        if (singleChild.keyEquals(node)) {
            return apply(singleChild)
        }
        return node
    }
}