package io.grule.node

import io.grule.token.TextPosition
import io.grule.token.Token
import io.grule.util.MultiMap
import io.grule.util.firstOrNull
import io.grule.util.getOrEmpty
import io.grule.util.lastOrNull

interface AstNode : KeyOwner, NodeStream<AstNode> {

    val text: String

    val tokens: Sequence<Token>

    val position: TextPosition get() = tokens.firstOrNull()?.position ?: TextPosition.Default

    val children: List<AstNode>

    val map: MultiMap<Any, AstNode>

    val size: Int get() = children.size

    fun isEmpty(): Boolean = children.isEmpty()

    fun isTerminal(): Boolean = children.isEmpty() && !tokens.none()

    fun isSingle(): Boolean = children.size == 1

    fun toStringExp(): String

    fun toStringTree(style: TreeStyle = TreeStyle.SOLID): String

    override fun transform(mapper: NodeMapper): AstNode {
        return mapper.apply(this)
    }

    operator fun get(index: Int): AstNode {
        return children[index]
    }

    fun all(): List<AstNode> {
        return children
    }

    fun all(key: Any): List<AstNode> {
        return map.getOrEmpty(KeyOwner.keyOf(key))
    }

    fun firstOrNull(): AstNode? {
        return children.firstOrNull()
    }

    fun firstOrNull(key: Any): AstNode? {
        return map.firstOrNull(KeyOwner.keyOf(key))
    }

    fun lastOrNull(): AstNode? {
        return children.lastOrNull()
    }

    fun lastOrNull(key: Any): AstNode? {
        return map.lastOrNull(KeyOwner.keyOf(key))
    }

    operator fun contains(key: Any): Boolean {
        return map.containsKey(KeyOwner.keyOf(key))
    }
}