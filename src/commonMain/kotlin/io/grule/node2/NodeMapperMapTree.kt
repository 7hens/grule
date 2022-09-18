package io.grule.node2

internal class NodeMapperMapTree(val mapper: (Node) -> Node) : NodeMapper {

    override fun apply(node: Node): Node {
        if (node.isEmpty()) {
            return mapper(node)
        }
        return node.newTree(node.list.map { apply(it) })
    }
}