package io.grule.matcher2.parser

import io.grule.node.AstNode

internal class ParserMatcherTransform(val parser: Parser, val transformation: AstNode.Transformation) : Parser {
    override val key: Any get() = parser.key

    override fun match(status: ParserMatcherStatus): ParserMatcherStatus {
        val result = parser.match(status)
        val resultNode = result.node.map { transformation.apply(it) }
        status.node.clear()
        status.node.merge(resultNode)
        return result
    }

    override fun toString(): String {
        return parser.toString()
    }
}