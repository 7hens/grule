package io.grule.node

internal class AstNodeMapTree(val mapper: (AstNode) -> AstNode) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        if (node.isTerminal) {
            return mapper(node)
        }
        val result = AstNode(node.key)
        node.all().forEach { result.add(map(it)) }
        return mapper(result)
    }
}