package io.grule.parser

import io.grule.node.AstNode

internal class ParserMatcherTransform(val parser: Parser, val transformation: AstNode.Transformation) : Parser {
    override val key: Any get() = parser.key

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val result = status.apply(parser)
        val resultNode = result.node.map { transformation.apply(it) }
        status.node.clear()
        status.node.merge(resultNode)
        return result
    }

    override fun toString(): String {
        return parser.toString()
    }
}