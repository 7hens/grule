package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.matcher2.Matcher
import io.grule.node.AstNode
import io.grule.node.AstNodeChain

open class ParserMatcherStatus private constructor(
    override val context: Matcher.Context,
    val position: Int,
    val node: AstNode
) : Matcher.Status<ParserMatcherStatus> {

    val tokenStreamProp: Matcher.Prop<TokenStream> get() = prop("tokenStream")

    val data: TokenStream get() = tokenStreamProp.get()!!

    val prevNode: Matcher.Prop<AstNode> get() = prop("prevNode")

    val nodeChainProp: Matcher.Prop<AstNodeChain> get() = prop("nodeChain")

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserMatcherStatus {
        node.add(childNode)
        return ParserMatcherStatus(context, position + 1, node)
    }

    fun withNode(node: AstNode): ParserMatcherStatus {
        return ParserMatcherStatus(context, position, node)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        return ParserMatcherStatus(context, position + 1, node)
    }

    override fun self(isMe: Boolean): ParserMatcherStatus {
        val nodeChain = nodeChainProp.get()!!
        val parentNode = AstNode.of(nodeChain.me)
        println("#self: nodeChain: $nodeChain")
        if (isMe) {
            parentNode.add(nodeChain.root)
            return withNode(parentNode)
        }
        return withNode(parentNode)
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val isolatedNode = AstNode.of(node)
        val result = matcher.match(withNode(isolatedNode))
        node.merge(isolatedNode)
        lastMatcher.set(matcher)
        return result.withNode(node)
    }

    override fun toString(): String {
        return "$lastMatcher #$position"
    }

    companion object {
        fun from(tokenStream: TokenStream, node: AstNode): ParserMatcherStatus {
            val instance = ParserMatcherStatus(Matcher.context(), 0, node)
            instance.tokenStreamProp.set(tokenStream)
            return instance
        }
    }
}