package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode

class ParserMatcherStatus(
    val data: TokenStream,
    val position: Int,
    val node: AstNode,
    override var lastMatcher: Matcher<ParserMatcherStatus>? = null
) : Matcher.Status<ParserMatcherStatus> {

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        node.add(childNode)
        return ParserMatcherStatus(data, position + 1, node, lastMatcher)
    }

    fun withNode(node: AstNode): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, node, lastMatcher)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(data, position + 1, node, lastMatcher)
    }

    override fun self(): ParserMatcherStatus {
        val parentNode = AstNode.of(node)
        parentNode.add(node)
        return withNode(parentNode)
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val result = matcher.match(this)
        result.lastMatcher = matcher
        return result
    }
}