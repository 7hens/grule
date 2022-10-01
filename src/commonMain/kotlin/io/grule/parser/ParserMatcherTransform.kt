package io.grule.parser

import io.grule.node.AstNode
import io.grule.node.NodeMapper
import io.grule.token.CharStream

internal class ParserMatcherTransform(
    val parser: Parser,
    val mapper: NodeMapper,
) : Parser {

    override val key: Any get() = parser.key

    override fun match(status: ParserStatus): ParserStatus {
        val result = parser.match(status)
        val resultNode = result.node.map { it.transform(mapper) }
        return result.withNode(resultNode)
    }

    override fun parse(source: CharStream): AstNode {
        return parse(source)
    }

    override fun toString(): String {
        return parser.toString()
    }
}