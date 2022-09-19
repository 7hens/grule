package io.grule.parser

import io.grule.lexer.Lexer
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
//        val lastNode = this.lastNode.get()!!
        println("parserStatus: $this")
        if (node.size > 1) {
            val body = node.subList(0, node.size - 1)
            val tail = node.last()
            return withNode(node.newNode(body + tail))
        }
//        val parentNode = AstNode.of(node)
//        parentNode.merge(lastNode)
//        lastNode.clear()
//        lastNode.add(parentNode)
//        newNode(node.all())
        return this
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