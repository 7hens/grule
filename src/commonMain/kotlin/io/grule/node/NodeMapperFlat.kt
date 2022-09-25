package io.grule.node

internal class NodeMapperFlat(private val predicate: (AstNode) -> Boolean) : NodeMapper {

    override fun apply(node: AstNode): AstNode {
        if (node.isEmpty()) {
            return node
        }
        return node.newNode(node.children
            .flatMap { if (predicate(it)) it.children else listOf(it) })
            .map { apply(it) }
    }
}