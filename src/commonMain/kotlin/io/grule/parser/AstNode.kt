package io.grule.parser

import io.grule.lexer.Token

open class AstNode(val rule: Any) {
    private val groups = mutableMapOf<Any, MutableList<AstNode>>()
    private val children = mutableListOf<AstNode>()

    open val isTerminal: Boolean = false

    open val firstToken: Token get() = children.first().firstToken
    open val lastToken: Token get() = children.last().lastToken

    open val text: String
        get() {
            return children.joinToString(" ") { it.text }
        }

    fun all(): List<AstNode> {
        return children
    }

    fun all(rule: Any): List<AstNode> {
        return groups[rule] ?: emptyList()
    }

    fun first(rule: Any): AstNode {
        return all(rule).first()
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
        groups.getOrPut(child.rule) { mutableListOf() }
            .add(child)
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
        if (rule is String) {
            return rule
        }
        return "$rule ($text)"
    }

    fun toStringTree(): String {
        if (children.isEmpty()) {
            return toString()
        }
        val result = StringBuilder(rule.toString())
        val childSize = children.size
        for ((index, child) in children.withIndex()) {
            if (childSize == index + 1) {
                result.append("\n  └ ")
                result.append(child.toStringTree().replace("\n", "\n    "))
            } else {
                result.append("\n  ├ ")
                result.append(child.toStringTree().replace("\n", "\n  │ "))
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
}
