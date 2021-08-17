package io.grule.lexer

import io.grule.parser.AstNode
import io.grule.parser.Parser

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
fun interface Scanner {
    fun scan(channel: TokenChannel)

    operator fun String.unaryMinus(): Lexer {
        return LexerCharSet(toList())
    }

    fun Parser.parse(input: CharStream): AstNode {
        return parse(TokenChannel(input, this@Scanner))
    }
}