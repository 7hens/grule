package io.grule.node

internal class AstNodeMapEach(val mapper: AstNode.Mapper) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        if (node.isTerminal) {
            return mapper.map(node)
        }
        val result = AstNode(node.key)
        node.all().forEach { result.add(map(it)) }
        return mapper.map(result)
    }
}