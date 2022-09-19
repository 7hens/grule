package io.grule.parser

import io.grule.node.NodeMapper

internal class ParserMatcherTransform(val parser: Parser, val mapper: NodeMapper) : Parser {
    override val key: Any get() = parser.key

    override fun match(status: ParserStatus): ParserStatus {
        val result = parser.match(status)
        val resultNode = result.node.transform(mapper)
        return result.withNode(resultNode)
    }

    override fun toString(): String {
        return parser.toString()
    }
}