//package io.grule.node
//
//internal class AstNodeFlat(private val predicate: (AstNode) -> Boolean) : AstNode.Transformation {
//
//    override fun apply(node: AstNode): AstNode {
//        flatNode(node)
//        return node
//    }
//
//    private fun flatNode(node: AstNode) {
//        if (node.isEmpty()) {
//            return
//        }
//        val tempNode = AstNode.of(node)
//        node.all().forEach { child ->
//            flatNode(child)
//            if (predicate(child)) {
//                tempNode.merge(child)
//            } else {
//                tempNode.add(child)
//            }
//        }
//        node.clear()
//        node.merge(tempNode)
//    }
//}