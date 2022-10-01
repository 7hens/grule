package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.node.AstNode
import io.grule.node.KeyOwner
import io.grule.node.NodeMapper
import io.grule.node.NodeStream
import io.grule.token.CharStream

interface Parser : ParserMatcher, ParserMatcherExt, KeyOwner, NodeStream<Parser> {

    fun parse(source: CharStream): AstNode

    fun parse(source: String): AstNode {
        return parse(CharStream.fromString(source))
    }

    override fun transform(mapper: NodeMapper): Parser {
        return ParserMatcherTransform(this, mapper)
    }

    fun flat(): Parser {
        return flat { it.key == this }
    }

    companion object {

        fun factory(lexer: Lexer): ParserFactory {
            return ParserFactory(lexer)
        }
    }
}