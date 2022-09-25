package io.grule.node

import io.grule.token.Token
import io.grule.util.MultiMap

internal class AstNodeBranch(
    override val key: Any,
    override val children: List<AstNode>,
) : AstNode {

    override val map: MultiMap<Any, AstNode>
            by lazy { children.groupBy { it.key } }

    override val text: String
            by lazy { children.joinToString(" ") { it.text } }

    override val tokens: Sequence<Token>
            by lazy { children.asSequence().flatMap { it.tokens } }

    override fun toString(): String {
        return children.joinToString(" ", "$key(", ")") { it.toString() }
    }

    override fun toStringExp(): String {
        if (size == 1) {
            return children.first().toStringExp()
        }
        return children.joinToString(" ", "(", ")") { it.toStringExp() }
    }

    override fun toStringTree(style: TreeStyle): String {
        if (isEmpty()) {
            return toString().replace("\n", "\\n")
        }
        val result = StringBuilder(key.toString())
        if (isSingle()) {
            val child = children.first()
            result.append("/").append(child.toStringTree(style))
        } else {
            for ((index, child) in children.withIndex()) {
                style.applyTo(result, child.toStringTree(style), index == size - 1)
            }
        }
        return result.toString()
    }
}