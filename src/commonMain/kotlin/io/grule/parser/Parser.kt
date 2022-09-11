package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.node.AstNode
import io.grule.node.AstNodeStream
import io.grule.node.KeyProvider
import io.grule.token.TokenStream

interface Parser : ParserMatcher, KeyProvider, AstNodeStream<Parser> {
    fun parse(tokenStream: TokenStream): AstNode {
        val mainParser = ParserMatcherDsl.run { this@Parser + Lexer.EOF }
        val node = AstNode.of(this)
        val status = ParserMatcherStatus.from(tokenStream, node)
        status.apply(mainParser)
        return node.first()
    }

    override fun transform(transformation: AstNode.Transformation): Parser {
        return ParserMatcherTransform(this, transformation)
    }

    fun flat(): Parser {
        return flat { it.key == this }
    }

    companion object {

        fun factory(): ParserFactory2 {
            return ParserFactory2()
        }
    }
}