//package io.grule.node
//
//internal class AstNodeForTree(val consumer: (AstNode) -> Unit) : AstNode.Transformation {
//
//    override fun apply(node: AstNode): AstNode {
//        consumer(node)
//        node.all().forEach { apply(it) }
//        return node
//    }
//}