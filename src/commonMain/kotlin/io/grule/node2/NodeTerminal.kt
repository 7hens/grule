package io.grule.node2

import io.grule.node.TreeStyle
import io.grule.token.Token
import io.grule.util.MultiMap

internal class NodeTerminal(override val key: Any, val token: Token) : Node {

    override val text: String get() = token.text

    override val tokens: List<Token> get() = listOf(token)

    override val list: List<Node>
        get() = emptyList()

    override val map: MultiMap<Any, Node>
        get() = emptyMap()

    override fun toString(): String {
        if (key is String) {
            return "$key"
        }
        return toStringLine(true)
    }

    override fun toStringLine(includesKey: Boolean): String {
        val prefix = if (includesKey) "$key" else ""
        return text
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("\n", "\\n")
            .let { if (includesKey) "$prefix($it)" else it }
    }

    override fun toStringTree(style: TreeStyle): String {
        return toString()
    }
}