package io.grule.node2

fun interface NodeMapper {
    fun apply(node: Node): Node
}