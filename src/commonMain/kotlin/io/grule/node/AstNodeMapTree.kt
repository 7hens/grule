//package io.grule.node
//
//internal class AstNodeMapTree(val mapper: (AstNode) -> AstNode) : AstNode.Transformation {
//
//    override fun apply(node: AstNode): AstNode {
//        if (node.isTerminal) {
//            return mapper(node)
//        }
//        val result = AstNode.of(node)
//        node.all().forEach { result.add(apply(it)) }
//        return mapper(result)
//    }
//}