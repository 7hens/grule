package io.grule.node

import io.grule.token.Token
import io.grule.util.MultiMap

internal class NodeTerminal(override val key: Any, val token: Token) : AstNode {

    override val text: String get() = token.text

    override val tokens: Sequence<Token> get() = sequenceOf(token)

    override val children: List<AstNode>
        get() = emptyList()

    override val map: MultiMap<Any, AstNode>
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