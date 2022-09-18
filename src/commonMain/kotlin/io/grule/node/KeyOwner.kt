package io.grule.node

import io.grule.node2.Node
import io.grule.node2.NodeBranch
import io.grule.node2.NodeTerminal
import io.grule.token.Token

interface KeyOwner {
    val key: Any

    fun keyEquals(other: Any): Boolean {
        return key == keyOf(other)
    }

    fun newEmpty(): Node {
        return newTree()
    }

    fun newTerminal(token: Token): Node {
        return NodeTerminal(key, token)
    }

    fun newSingle(node: Node): Node {
        return newTree(listOf(node))
    }

    fun newTree(nodes: Iterable<Node>): Node {
        return NodeBranch(key, nodes.toList())
    }

    fun newTree(vararg nodes: Node): Node {
        return newTree(listOf(*nodes))
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