package io.grule.node

import io.grule.lexer.Token

open class AstNode(val key: Any) : AstNodeStream<AstNode> {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false
    open val firstToken: Token get() = children.first().firstToken
    open val lastToken: Token get() = children.last().lastToken

    open val text: String get() = children.joinToString(" ") { it.text }

    override fun map(mapper: Mapper): AstNode = mapper.map(this)

    val size: Int get() = children.size
    fun isEmpty(): Boolean = children.isEmpty()
    fun isNotEmpty(): Boolean = !isEmpty()

    private fun isSingleChain(): Boolean {
        return when {
            children.isEmpty() -> true
            children.size > 1 -> false
            else -> children.first().isSingleChain()
        }
    }

    fun all(): List<AstNode> = children
    fun all(rule: Any): List<AstNode> = groups[rule] ?: emptyList()
    operator fun get(index: Int): AstNode = children[index]
    operator fun contains(rule: Any): Boolean = groups[rule]?.isNotEmpty() ?: false

    fun first(): AstNode = children.first()
    fun first(rule: Any): AstNode = all(rule).first()
    fun firstOrNull(): AstNode? = children.firstOrNull()
    fun firstOrNull(rule: Any): AstNode? = all(rule).firstOrNull()

    fun last(): AstNode = children.last()
    fun last(rule: Any): AstNode = all(rule).last()
    fun lastOrNull(): AstNode? = children.lastOrNull()
    fun lastOrNull(rule: Any): AstNode? = all(rule).lastOrNull()

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
