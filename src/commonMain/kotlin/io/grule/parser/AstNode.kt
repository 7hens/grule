package io.grule.parser

import io.grule.lexer.Token

open class AstNode(val rule: Any) {
    private val children = mutableMapOf<Any, MutableList<AstNode>>()

    var childCount = 0
        private set

    open val text: String get() = rule.toString()

    operator fun get(rule: Any): List<AstNode> {
        return list(rule)
    }

    fun m(rule: Any): AstNode {
        return list(rule).first()
    }

    operator fun contains(rule: Any): Boolean {
        return children[rule]?.isNotEmpty() ?: false
    }

    private fun list(rule: Any): MutableList<AstNode> {
        return children.getOrPut(rule) { mutableListOf() }
    }

    open fun add(child: AstNode) {
        childCount++
        list(child.rule).add(child)
    }

    fun merge(node: AstNode) {
        if (node is Leaf) {
            add(node)
            return
        }
        childCount += node.childCount
        for ((rule, values) in node.children) {
            list(rule).addAll(values)
        }
    }

    override fun toString(): String {
        return children.entries.toPrettyString {
            "${it.key} = ${it.value.toPrettyString()}"
        }
    }

    private fun <T> Iterable<T>.toPrettyString(transform: ((T) -> CharSequence)? = null): String {
        if (count() == 0) return "[]"
        return joinToString("\n", "[\n", transform = transform)
            .replace("\n", "\n  ") + "\n]"
    }

    open class Leaf(rule: Any, val token: Token) : AstNode(rule) {
        override val text: String get() = token.text

        override fun add(child: AstNode) {
            throw UnsupportedOperationException()
        }

        override fun toString(): String {
            return token.toString()
        }
    }
}
