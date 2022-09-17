package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.matcher.Context
import io.grule.matcher.Matcher
import io.grule.matcher.Prop
import io.grule.matcher.Status
import io.grule.node.AstNode
import io.grule.node.AstNodeChain
import io.grule.token.Token
import io.grule.token.TokenStream

open class ParserMatcherStatus private constructor(
    override val context: Context,
    val position: Int,
    val node: AstNode
) : Status<ParserMatcherStatus> {

    val tokenStreamProp: Prop<TokenStream> get() = prop("tokenStream")

    val data: TokenStream get() = tokenStreamProp.get()!!

    val lastNode: Prop<AstNode> get() = prop("lastNode")

    val nodeChainProp: Prop<AstNodeChain> get() = prop("nodeChain")

    val lastNodeChainProp: Prop<AstNodeChain> get() = prop("lastNodeChain")

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

    override fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserMatcherStatus {
        if (data.peek() == Lexer.EOF) {
            panic(Lexer.EOF)
        }
        return ParserMatcherStatus(context, position + 1, node)
    }

    override fun self(): ParserMatcherStatus {
        val lastNode = this.lastNode.get()!!
        val parentNode = AstNode.of(node)
        parentNode.merge(lastNode)
        lastNode.clear()
        lastNode.add(parentNode)
        return this
    }

    override fun apply(matcher: Matcher<ParserMatcherStatus>): ParserMatcherStatus {
        val isolatedNode = AstNode.of(node)
        lastNodeChainProp.set { it.addChild(isolatedNode) }
        val result = matcher.match(withNode(isolatedNode))
        if (matcher.isNode) {
            if (isolatedNode.isNotEmpty()) {
                val firstChild = isolatedNode.first()
                if (isolatedNode.size == 1 && firstChild.key == node.key) {
                    node.add(firstChild)
                } else {
                    node.add(isolatedNode)
                }
            }
        } else {
            node.merge(isolatedNode)
        }
        lastMatcher.set(matcher)
        lastNode.set(node)
        lastNodeChainProp.set { it.pop() }
        return result.withNode(node)
    }

    override fun toString(): String {
        return "${lastMatcher.get()} #$position"
    }

    companion object {
        fun from(tokenStream: TokenStream, node: AstNode): ParserMatcherStatus {
            val instance = ParserMatcherStatus(Matcher.context(), 0, node)
            instance.tokenStreamProp.set(tokenStream)
            instance.nodeChainProp.set(AstNodeChain.of(node))
            instance.lastNodeChainProp.set(AstNodeChain.of(node))
            return instance
        }
    }
}