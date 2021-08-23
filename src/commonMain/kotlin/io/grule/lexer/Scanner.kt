package io.grule.lexer

import io.grule.parser.AstNode
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
fun interface Scanner {
    fun scan(tokenStream: TokenStream)

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    operator fun String.unaryMinus(): Lexer {
        return LexerCharSet(toList())
    }

    fun Parser.parse(charStream: CharStream): AstNode {
        return parse(tokenStream(charStream))
    }
}