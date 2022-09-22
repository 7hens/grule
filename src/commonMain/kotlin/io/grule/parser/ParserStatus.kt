package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.node.AstNode
import io.grule.token.Token
import io.grule.token.TokenStream

open class ParserStatus(
    override val key: Any,
    val node: AstNode,
    val data: TokenStream,
    val position: Int = 0,
) : Status<ParserStatus> {

    override fun withKey(key: Any): ParserStatus {
        return ParserStatus(key, node, data, position)
    }

    fun withNode(node: AstNode): ParserStatus {
        return ParserStatus(key, node, data, position)
    }

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserStatus {
        return ParserStatus(key, node + childNode, data, position + 1)
    }

    override fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserStatus {
        if (data.peek() == Lexer.EOF) {
            panic(Lexer.EOF)
        }
        return ParserStatus(key, node, data, position + 1)
    }

    override fun self(): ParserStatus {
        println("self: $this,")
        if (node.size > 1) {
            return withNode(node.newNode(node))
        }
        return this
    }

    override fun self(matcher: Matcher<ParserStatus>): ParserStatus {
        println("self_m: $this,     $matcher")
        val result = matcher.match(withNode(node.newNode()))
        return result.withNode(node.newNode(trimSingle(node + trimSingle(result.node))))
    }

    private fun trimSingle(node: AstNode): AstNode {
        if (!node.isSingle()) {
            return node
        }
        val singleChild = node.first()
        if (singleChild.keyEquals(node)) {
            return trimSingle(singleChild)
        }
        return node
    }

//    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
//        val isolatedNode = AstNode.of(node)
//        lastNodeChainProp.set { it.addChild(isolatedNode) }
//        val result = matcher.match(withNode(isolatedNode))
//        if (matcher.isNode) {
//            if (isolatedNode.isNotEmpty()) {
//                val firstChild = isolatedNode.first()
//                if (isolatedNode.size == 1 && firstChild.key == node.key) {
//                    node.add(firstChild)
//                } else {
//                    node.add(isolatedNode)
//                }
//            }
//        } else {
//            node.merge(isolatedNode)
//        }
//        lastMatcher.set(matcher)
//        lastNode.set(node)
//        lastNodeChainProp.set { it.pop() }
//        return result.withNode(node)
//    }

    override fun toString(): String {
        return "#$position, @$key, ${node.toStringLine(true)}"
    }
}