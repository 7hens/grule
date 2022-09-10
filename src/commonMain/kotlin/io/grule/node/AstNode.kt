package io.grule.node

import io.grule.lexer.Token

open class AstNode private constructor(keyProvider: KeyProvider) : AstNodeStream<AstNode>, KeyProvider by keyProvider {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false
    open val firstToken: Token?
        get() {
            return children.asSequence().map { it.firstToken }.firstOrNull { it != null }
        }
    open val lastToken: Token?
        get() {
            return children.reversed().asSequence().map { it.lastToken }.firstOrNull { it != null }
        }

    open val text: String get() = children.joinToString(" ") { it.text }

    override fun transform(transformation: Transformation): AstNode = transformation.apply(this)

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
        require(!isTerminal) { "Cannot add child to terminal node (${toStringLine()})" }
        children.add(child)
        groups.getOrPut(child.key) { mutableListOf() }
            .add(child)
    }

    fun addHead(child: AstNode) {
        require(!isTerminal)
        children.add(0, child)
        groups.getOrPut(child.key) { mutableListOf() }
            .add(0, child)
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
        val result = AstNode(this)
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

    fun copy(): AstNode {
        return subList(0)
    }

    override fun toString(): String {
        if (key is String) {
            return "($key)"
        }
        return "$key ($text)"
    }

    fun toStringLine(): String {
        if (isTerminal) {
            return text.replace("(", "\\(").replace(")", "\\)").replace("\n", "\\n")
        }
        if (size == 1) {
            return first().toStringLine()
        }
        return children.joinToString(" ", "(", ")") { it.toStringLine() }
    }

    fun toStringTree(style: TreeStyle = TreeStyle.SOLID): String {
        if (isEmpty()) {
            return toString().replace("\n", "\\n")
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
        fun of(key: Any): AstNode {
            return AstNode(KeyProvider(key))
        }

        fun of(key: Any, token: Token): AstNode {
            return Terminal(KeyProvider(key), token)
        }

        fun of(key: Any, vararg elements: AstNode): AstNode {
            return of(key, listOf(*elements))
        }

        fun of(key: Any, elements: Iterable<AstNode>): AstNode {
            return of(key).apply { addAll(elements) }
        }
    }

    private class Terminal(rule: KeyProvider, token: Token) : AstNode(rule) {
        override val isTerminal: Boolean = true
        override val firstToken: Token = token
        override val lastToken: Token = token
        override val text: String get() = firstToken.text
    }

    fun interface Transformation {
        fun apply(node: AstNode): AstNode
    }

    object DefaultComparator : Comparator<AstNode> {
        override fun compare(a: AstNode, b: AstNode): Int {
            val firstPriority = OperatorPriority.of(a.firstToken!!.text)
            val secondPriority = OperatorPriority.of(b.firstToken!!.text)
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
