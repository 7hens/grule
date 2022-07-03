package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserNode(val parser: Parser, val mapper: AstNode.Mapper) : Parser {
    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        val node = AstNode(parentNode.key)
        val result = parser.parse(tokenStream, node, offset)
        parentNode.merge(mapper.map(node))
        return result
    }

    override fun toString(): String {
        return parser.toString()
    }
}