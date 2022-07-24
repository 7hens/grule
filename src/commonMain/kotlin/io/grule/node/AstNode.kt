package io.grule.node

import io.grule.lexer.Token

open class AstNode(val key: Any) : AstNodeStream<AstNode> {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false
    open val firstToken: Token get() = children.first().firstToken
    open val lastToken: Token get() = children.last().lastToken

    open val text: String get() = children.joinToString(" ") { it.text }

    override fun transform(mapper: Mapper): AstNode = mapper.map(this)

    val size: Int get() = children.size
    fun isEmpty(): Boolean = children.isEmpty()
    fun isNotEmpty(): Boolean = !isEmpty()

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

    fun addAll(elements: Iterable<AstNode>) {
        elements.forEach { add(it) }
    }

    fun remove(child: AstNode) {
        require(!isTerminal)
        if (!groups.containsKey(child.key)) {
            return
        }
        children.remove(child)
        groups.getValue(child.key).remove(child)
    }

    fun subList(fromIndex: Int, toIndex: Int = size): AstNode {
        val result = AstNode(key)
        result.addAll(all().subList(fromIndex, toIndex))
        return result
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

    fun toStringLine(): String {
        if (isTerminal) {
            return text
        }
        if (size == 1) {
            return first().toStringLine()
        }
        return children.joinToString(" ", "(", ")") { it.toStringLine() }
    }

    fun toStringTree(style: TreeStyle = TreeStyle.SOLID): String {
        if (isEmpty()) {
            return toString()
        }
        val result = StringBuilder(key.toString())
        val childSize = children.size
        if (childSize == 1) {
            val child = children.first()
            result.append("/").append(child.toStringTree(style))
        } else {
            for ((index, child) in children.withIndex()) {
                style.applyTo(result, child.toStringTree(style), childSize == index + 1)
            }
        }
        return result.toString()
    }

    companion object {
        fun of(key: Any, vararg elements: AstNode): AstNode {
            return of(key, listOf(*elements))
        }

        fun of(key: Any, elements: Iterable<AstNode>): AstNode {
            return AstNode(key).apply { addAll(elements) }
        }
    }

    internal class Terminal(rule: Any, token: Token) : AstNode(rule) {
        override val isTerminal: Boolean = true
        override val firstToken: Token = token
        override val lastToken: Token = token
        override val text: String get() = firstToken.text
    }

    fun interface Mapper {
        fun map(node: AstNode): AstNode
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
