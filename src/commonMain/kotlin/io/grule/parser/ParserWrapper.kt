package io.grule.parser

import io.grule.lexer.TokenStream
import io.grule.node.AstNode

internal abstract class ParserWrapper : Parser {
    abstract val base: Parser

    override fun parse(tokenStream: TokenStream, parentNode: AstNode, offset: Int): Int {
        return base.parse(tokenStream, parentNode, offset)
    }

    override val key: Any
        get() = base.key

    override fun toString(): String {
        return base.toString()
    }

    companion object {
        fun of(parser: Parser): ParserWrapper {
            return object : ParserWrapper() {
                override val base: Parser
                    get() = parser
            }
        }
    }
}