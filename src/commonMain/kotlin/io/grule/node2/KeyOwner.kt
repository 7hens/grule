package io.grule.node2

import io.grule.token.Token

interface KeyOwner {
    val key: Any

    fun keyEquals(other: Any): Boolean {
        return key == keyOf(other)
    }

    fun newNode(token: Token): Node {
        return NodeTerminal(key, token)
    }

    fun newNode(nodes: Iterable<Node>): Node {
        return NodeBranch(key, nodes.toList())
    }

    fun newNode(vararg nodes: Node): Node {
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