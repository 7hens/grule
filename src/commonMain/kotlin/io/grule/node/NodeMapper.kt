package io.grule.node

fun interface NodeMapper {
    fun apply(node: AstNode): AstNode
}