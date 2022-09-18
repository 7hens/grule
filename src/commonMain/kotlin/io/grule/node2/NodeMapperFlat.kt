package io.grule.node2

internal class NodeMapperFlat(private val predicate: (Node) -> Boolean) : NodeMapper {

    override fun apply(node: Node): Node {
        if (node.isEmpty()) {
            return node
        }
        return node.newTree(node.list
            .flatMap { if (predicate(it)) it.list else listOf(it) })
            .map { apply(it) }
    }
}