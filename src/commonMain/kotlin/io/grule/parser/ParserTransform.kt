package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal class ParserTransform(val parser: Parser, val transformation: AstNode.Transformation) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode.of(parentNode)
        val result = parser.parse(tokenStream, node, offset)
        parentNode.merge(node.map { transformation.apply(it) })
        return result
    }

    override fun toString(): String {
        return parser.toString()
    }
}