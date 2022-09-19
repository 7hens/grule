package io.grule.node

import io.grule.token.Token

interface KeyOwner {
    val key: Any

    fun keyEquals(other: Any): Boolean {
        return key == keyOf(other)
    }

    fun newNode(token: Token): AstNode {
        return NodeTerminal(key, token)
    }

    fun newNode(nodes: Iterable<AstNode>): AstNode {
        return NodeBranch(key, nodes.toList())
    }

    fun newNode(vararg nodes: AstNode): AstNode {
        return newNode(listOf(*nodes))
    }

    companion object {
        fun keyOf(value: Any): Any {
            return invoke(value).key
        }

        operator fun invoke(value: Any): KeyOwner {
            return (value as? KeyOwner) ?: Impl(value)
        }
    }

    private class Impl(override val key: Any) : KeyOwner
}