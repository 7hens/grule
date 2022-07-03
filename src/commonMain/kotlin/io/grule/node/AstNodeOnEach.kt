package io.grule.node

internal class AstNodeOnEach(val consumer: AstNode.Consumer) : AstNode.Mapper {

    override fun map(node: AstNode): AstNode {
        consumer.consume(node)
        node.all().forEach { map(it) }
        return node
    }
}