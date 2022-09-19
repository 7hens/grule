package io.grule.node2

import io.grule.token.Token
import io.grule.util.MultiMap

internal class NodeBranch(
    override val key: Any,
    override val list: List<Node>
) : Node {

    override val map: MultiMap<Any, Node>
            by lazy { list.groupBy { it.key } }

    override val text: String
            by lazy { list.joinToString(" ") { it.text } }

    override val tokens: List<Token>
            by lazy { list.flatMap { it.tokens } }

    override fun toString(): String {
        if (key is String) {
            return "($key)"
        }
        return "$key ($text)"
    }

    override fun toStringLine(includesKey: Boolean): String {
        val prefix = if (includesKey) "$key" else ""
        if (size == 1 && !includesKey) {
            return list.first().toStringLine(includesKey)
        }
        return list.joinToString(" ", "$prefix(", ")") { it.toStringLine(includesKey) }
    }

    override fun toStringTree(style: TreeStyle): String {
        if (isEmpty()) {
            return toString().replace("\n", "\\n")
        }
        val result = StringBuilder(key.toString())
        if (isSingle()) {
            val child = list.first()
            result.append("/").append(child.toStringTree(style))
        } else {
            for ((index, child) in list.withIndex()) {
                style.applyTo(result, child.toStringTree(style), index == size - 1)
            }
        }
        return result.toString()
    }
}