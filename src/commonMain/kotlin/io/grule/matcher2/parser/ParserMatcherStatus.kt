package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode
import io.grule.node.AstNodeChain

open class ParserMatcherStatus(
    val data: TokenStream,
    val position: Int,
    val contextNodes: AstNodeChain,
    val node: AstNode,
    override val lastMatcher: Matcher<ParserMatcherStatus>? = null
) : Matcher.Status<ParserMatcherStatus> {

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        node.add(childNode)
        return ParserMatcherStatus(data, position + 1, contextNodes, node, lastMatcher)
    }

    fun withNode(node: AstNode): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, contextNodes, node, lastMatcher)
    }

    override fun withMatcher(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, contextNodes, node, matcher)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(data, position + 1, contextNodes, node, lastMatcher)
    }

    override fun self(isMe: Boolean): ParserMatcherStatus {
        val parentNode = AstNode.of(contextNodes.me)
        println("#self: nodeChain: $contextNodes")
        if (isMe) {
            parentNode.add(contextNodes.root)
            return withNode(parentNode)
        }
        return withNode(parentNode)
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val isolatedNode = AstNode.of(contextNodes.me)
        val result = matcher.match(withNode(isolatedNode))
        node.merge(isolatedNode)
        return result.withNode(node).withMatcher(matcher)
    }

    override fun toString(): String {
        return "${contextNodes}, $lastMatcher #$position"
    }
}