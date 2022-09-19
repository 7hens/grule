package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.node.AstNode
import io.grule.node.KeyOwner
import io.grule.node.NodeMapper
import io.grule.node.NodeStream
import io.grule.token.TokenStream

interface Parser : ParserMatcher, ParserMatcherExt, KeyOwner, NodeStream<Parser> {
    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = ParserDsl.run { this@Parser + Lexer.EOF }
        val status = ParserStatus(key, newNode(), tokenStream)
        return mainParser.match(status).node.first()
    }

    override fun transform(mapper: NodeMapper): Parser {
        return ParserMatcherTransform(this, mapper)
    }

    fun flat(): Parser {
        return flat { it.key == this }
    }

    companion object {

        fun factory(): ParserFactory {
            return ParserFactory()
        }
    }
}