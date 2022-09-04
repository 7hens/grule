package io.grule.matcher2.parser

import io.grule.lexer.Token
import io.grule.lexer.TokenStream
import io.grule.node.AstNode

interface ParserMatcherContext {
    val position: Int

    val node: AstNode

    fun peek(offset: Int = 0): Token

    fun next(childNode: AstNode): ParserMatcherContext {
        node.add(childNode)
        return Delegate(this, 1, node)
    }

    fun withNode(node: AstNode): ParserMatcherContext {
        return Delegate(this, 0, node)
    }

    fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    companion object {
        fun from(tokenStream: TokenStream, node: AstNode, position: Int = 0): ParserMatcherContext {
            return Impl(tokenStream, node, position)
        }
    }

    private class Delegate(val prev: ParserMatcherContext, val count: Int, override val node: AstNode) :
        ParserMatcherContext {
        override val position: Int get() = prev.position + count

        override fun peek(offset: Int): Token {
            return prev.peek(position + offset)
        }
    }

    private class Impl(
        val tokenStream: TokenStream,
        override val node: AstNode,
        override val position: Int,
    ) : ParserMatcherContext {
        override fun peek(offset: Int): Token {
            return tokenStream.peek(position + offset)
        }
    }
}