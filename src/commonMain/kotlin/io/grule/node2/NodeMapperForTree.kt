package io.grule.node2

internal class NodeMapperForTree(val consumer: (Node) -> Unit) : NodeMapper {

    override fun apply(node: Node): Node {
        consumer(node)
        node.list.forEach { apply(it) }
        return node
    }
}