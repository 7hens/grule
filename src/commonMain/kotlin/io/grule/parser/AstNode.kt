package io.grule.parser

import io.grule.lexer.Token

open class AstNode(val key: Any) : AstNodeStream<AstNode> {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false
    open val firstToken: Token get() = children.first().firstToken
    open val lastToken: Token get() = children.last().lastToken

    open val text: String
        get() {
            return children.joinToString(" ") { it.text }
        }

    override fun node(mapper: Mapper): AstNode {
        return mapper.map(this)
    }

    fun isEmpty(): Boolean = children.isEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()

    private fun isSingleChain(): Boolean {
        return when {
            children.isEmpty() -> true
            children.size > 1 -> false
            else -> children.first().isSingleChain()
        }
    }

    val size: Int get() = children.size

    fun all(): List<AstNode> {
        return children
    }

    fun all(rule: Any): List<AstNode> {
        return groups[rule] ?: emptyList()
    }

    fun first(rule: Any): AstNode {
        return all(rule).first()
    }

    fun last(rule: Any): AstNode {
        return all(rule).last()
    }

    fun firstOrNull(rule: Any): AstNode? {
        return all(rule).firstOrNull()
    }

    fun lastOrNull(rule: Any): AstNode? {
        return all(rule).lastOrNull()
    }

    operator fun get(index: Int): AstNode {
        return children[index]
    }

    operator fun contains(rule: Any): Boolean {
        return groups[rule]?.isNotEmpty() ?: false
    }

    fun add(child: AstNode) {
        require(!isTerminal)
        children.add(child)
        groups.getOrPut(child.key) { mutableListOf() }
            .add(child)
    }

    fun remove(child: AstNode) {
        require(!isTerminal)
        if (!groups.containsKey(child.key)) {
            return
        }
        children.remove(child)
        groups.getValue(child.key).remove(child)
    }

    fun clear() {
        children.clear()
        groups.clear()
    }

    fun merge(node: AstNode) {
        if (node.isTerminal) {
            add(node)
            return
        }
        for (child in node.children) {
            add(child)
        }
    }

    override fun toString(): String {
        if (key is String) {
            return "($key)"
        }
        return "$key ($text)"
    }

    fun toStringTree(style: TreeStyle = TreeStyle.SOLID): String {
        if (isEmpty()) {
            return toString()
        }
        if (isSingleChain()) {
            return "$key/${children.first().toStringTree()}"
        }
        val result = StringBuilder(key.toString())
        val childSize = children.size
        for ((index, child) in children.withIndex()) {
            style.applyTo(result, child.toStringTree(style), childSize == index + 1)
        }
        return result.toString()
    }

    internal class Terminal(rule: Any, token: Token) : AstNode(rule) {
        override val isTerminal: Boolean = true
        override val firstToken: Token = token
        override val lastToken: Token = token
        override val text: String get() = firstToken.text
    }

    fun interface Consumer {
        fun consume(node: AstNode)
    }

    fun interface Mapper {
        fun map(node: AstNode): AstNode
    }

    fun interface Predicate {
        fun test(node: AstNode): Boolean
    }

    object DefaultComparator : Comparator<AstNode> {
        override fun compare(a: AstNode, b: AstNode): Int {
            val firstPriority = OperatorPriority.of(a.firstToken.text)
            val secondPriority = OperatorPriority.of(b.firstToken.text)
            return firstPriority.ordinal - secondPriority.ordinal
        }
    }

    private enum class OperatorPriority {
        MIN, SEMICOLON, QUERY, OR, XOR, AND, EQ, REL, PLUS, TIMES, MAX;

        companion object {
            private val priorities = mapOf(
                ";" to SEMICOLON,
                "?" to QUERY,
                "|" to OR,
                "^" to XOR,
                "&" to AND,
                "=" to EQ, "!" to EQ,
                ">" to REL, "<" to REL,
                "+" to PLUS, "-" to PLUS,
                "*" to TIMES, "/" to TIMES, "%" to TIMES,
            )

            fun of(operator: String): OperatorPriority {
                val first = operator.substring(0, 1)
                return priorities[first] ?: MIN
            }
        }
    }
}
