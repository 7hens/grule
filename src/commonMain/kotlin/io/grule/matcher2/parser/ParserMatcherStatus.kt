package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode

class ParserMatcherStatus(
    val data: TokenStream,
    val position: Int,
    val node: AstNode
) : Matcher.Status<ParserMatcherStatus> {

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        node.add(childNode)
        return ParserMatcherStatus(data, position + 1, node)
    }

    fun withNode(node: AstNode): ParserMatcherStatus {
        return ParserMatcherStatus(data, position, node)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override var lastMatcher: Matcher<ParserMatcherStatus>? = null
        private set

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(data, position + 1, node)
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val result = matcher.match(this)
        lastMatcher = matcher
        return result
    }
}