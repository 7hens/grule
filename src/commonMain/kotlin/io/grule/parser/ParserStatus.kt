package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.node.AstNode
import io.grule.token.Token
import io.grule.token.TokenStream
import io.grule.util.Log

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
        val result = matcher.match(withNode(node.newNode()))
        val newNode = node + result.node.trimSingle()
        Log.debug {
            "self: ${node.toStringLine()},     $matcher" +
                    "\n     -> ${result.node.toStringLine()}" +
                    "\n     => ${newNode.toStringLine()}"
        }
        return result.withNode(newNode)
    }

    override fun selfPartial(matcher: Matcher<ParserStatus>): ParserStatus {
        val result = matcher.match(withNode(node.newNode()))
        val newNode = node.newNode(node + result.node.all())
        Log.debug {
            "selfPartial: ${node.toStringLine()},     $matcher" +
                    "\n     -> ${result.node.toStringLine()}" +
                    "\n     => ${newNode.toStringLine()}"
        }
        return result.withNode(newNode)
    }

    private fun resolveSelfNode(prevNode: AstNode, resultNode: AstNode, partial: Boolean): AstNode {
        val trimmedPrevNode = trimSinglePrev(prevNode)
        val trimmedResultNode = trimSingle(resultNode)
        if (partial && !trimmedResultNode.isTerminal()) {
            return trimmedPrevNode + trimmedResultNode.all()
        }
        return trimmedPrevNode + trimmedResultNode.trimSingle()
    }

    private fun trimSinglePrev(node: AstNode): AstNode {
        if (!node.isSingle()) {
            return node
        }
        val singleChild = node.first()
        return node.newNode(trimSingle(singleChild))
    }

    private fun trimSingle(node: AstNode): AstNode {
//        if (true) return node
        if (!node.isSingle()) {
            return node
        }
        val singleChild = node.first()
        if (singleChild.keyEquals(node)) {
            return singleChild
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
        return "#$position, @$key, ${node.toStringLine()}"
    }
}