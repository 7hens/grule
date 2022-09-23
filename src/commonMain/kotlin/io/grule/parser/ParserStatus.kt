package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.LexerEOF.key
import io.grule.matcher.Matcher
import io.grule.matcher.Status
import io.grule.node.AstNode
import io.grule.token.Token
import io.grule.token.TokenStream
import io.grule.util.Log

open class ParserStatus(
    val node: AstNode,
    val data: TokenStream,
    val position: Int = 0,
) : Status<ParserStatus> {

    fun withNode(node: AstNode): ParserStatus {
        return ParserStatus(node, data, position)
    }

    fun peek(offset: Int = 0): Token {
        return data.peek(position + offset)
    }

    fun next(childNode: AstNode): ParserStatus {
        return ParserStatus(node + childNode, data, position + 1)
    }

    override fun panic(rule: Any): Nothing {
        throw ParserMatcherException(this, rule)
    }

    override fun next(): ParserStatus {
        if (data.peek() == Lexer.EOF) {
            panic(Lexer.EOF)
        }
        return ParserStatus(node, data, position + 1)
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

    override fun toString(): String {
        return "#$position, @$key, ${node.toStringLine()}"
    }
}