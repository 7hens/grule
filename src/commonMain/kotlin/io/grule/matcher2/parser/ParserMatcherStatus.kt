package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode

class ParserMatcherStatus(
    val data: TokenStream,
    val position: Int,
    val rootNode: AstNode,
    val lastNode: AstNode,
    override var lastMatcher: Matcher<ParserMatcherStatus>? = null
) : Matcher.Status<ParserMatcherStatus> {

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        lastNode.add(childNode)
        return ParserMatcherStatus(data, position + 1, rootNode, lastNode, lastMatcher)
    }

    fun withNode(node: AstNode): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, rootNode, node, lastMatcher)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(data, position + 1, rootNode, lastNode, lastMatcher)
    }

    override fun self(isMe: Boolean): ParserMatcherStatus {
        if (isMe) {
            val parentNode = AstNode.of(lastNode)
            parentNode.add(lastNode)
            return withNode(parentNode)
        }

        val childNode = AstNode.of(lastNode)
        lastNode.add(childNode)
        if (rootNode !== lastNode) {
            rootNode.add(childNode)
        }
        return withNode(childNode)
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val result = matcher.match(this)
        result.lastMatcher = matcher
        return result
    }

    override fun toString(): String {
        return "$lastNode, $lastMatcher #$position"
    }
}