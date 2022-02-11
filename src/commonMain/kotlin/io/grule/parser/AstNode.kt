package io.grule.parser

import io.grule.lexer.Token

open class AstNode(val key: Any) {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false
    open val firstToken: Token get() = children.first().firstToken
    open val lastToken: Token get() = children.last().lastToken

    open val text: String
        get() {
            return children.joinToString(" ") { it.text }
        }

    fun isEmpty(): Boolean = children.isEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()
    
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

    fun toStringTree(): String {
        if (children.isEmpty()) {
            return toString()
        }
        val result = StringBuilder(key.toString())
        val childSize = children.size
        for ((index, child) in children.withIndex()) {
            if (childSize == index + 1) {
                result.append("\n └─ ")
                result.append(child.toStringTree().replace("\n", "\n    "))
            } else {
                result.append("\n ├─ ")
                result.append(child.toStringTree().replace("\n", "\n │  "))
            }
        }
        return result.toString()
    }

    internal class Terminal(rule: Any, token: Token) : AstNode(rule) {
        override val isTerminal: Boolean = true
        override val firstToken: Token = token
        override val lastToken: Token = token
        override val text: String get() = firstToken.text
    }

    open class Visitor {
        open fun visit(node: AstNode) {
            for (child in node.all()) {
                visit(child)
            }
        }
    }

    object DefaultComparator : Comparator<AstNode> {
        override fun compare(a: AstNode, b: AstNode): Int {
            val firstPriority = OperatorPriority.of(a.firstToken.text)
            val secondPriority = OperatorPriority.of(b.firstToken.text)
            return firstPriority.ordinal - secondPriority.ordinal
        }
    }

    private enum class OperatorPriority {
        MIN, QUERY, OR, XOR, AND, EQ, REL, PLUS, TIMES, MAX;

        companion object {
            private val priorities = mapOf(
                "?" to QUERY, ";" to QUERY,
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
