package io.grule.node2

import io.grule.token.Token
import io.grule.util.MultiMap

interface Node : KeyOwner, NodeStream<Node> {

    val text: String

    val tokens: List<Token>

    val list: List<Node>

    val map: MultiMap<Any, Node>

    val size: Int get() = list.size

    fun isEmpty(): Boolean = list.isEmpty()

    fun isTerminal(): Boolean = list.isEmpty() && tokens.isNotEmpty()

    fun isSingle(): Boolean = list.size == 1

    fun toStringLine(includesKey: Boolean = false): String

    fun toStringTree(style: TreeStyle = TreeStyle.SOLID): String

    override fun transform(mapper: NodeMapper): Node {
        return mapper.apply(this)
    }
}