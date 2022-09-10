package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode
import io.grule.node.AstNodeChain

open class ParserMatcherStatus(
    val data: TokenStream,
    val position: Int,
    val nodeChain: AstNodeChain,
    override val lastMatcher: Matcher<ParserMatcherStatus>? = null
) : Matcher.Status<ParserMatcherStatus> {

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        nodeChain.me.add(childNode)
        return ParserMatcherStatus(data, position + 1, nodeChain, lastMatcher)
    }

    fun withNode(fn: AstNodeChain.() -> AstNodeChain): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, fn(nodeChain), lastMatcher)
    }

    override fun withMatcher(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, nodeChain, matcher)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(data, position + 1, nodeChain, lastMatcher)
    }

    override fun self(isMe: Boolean): ParserMatcherStatus {
        val parentNode = AstNode.of(nodeChain.me)
        if (isMe) {
            parentNode.add(nodeChain.root)
            return withNode { AstNodeChain.of(parentNode) }
        }
        return withNode { add(parentNode) }
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val node = AstNode.of(nodeChain.me)
        val result = matcher.match(withNode { AstNodeChain.of(node) })
        nodeChain.me.merge(node)
        return result.withNode { nodeChain }.withMatcher(matcher)
    }

    override fun toString(): String {
        return "${nodeChain}, $lastMatcher #$position"
    }
}