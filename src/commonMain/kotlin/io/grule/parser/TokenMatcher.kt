package io.grule.parser

import io.grule.lexer.Lexer
import io.grule.lexer.Token

interface TokenMatcher {
    val rule: Any

    fun matches(token: Token): Boolean

    fun node(token: Token): AstNode {
        return AstNode.Leaf(rule, token)
    }

    companion object {
        operator fun invoke(text: String): TokenMatcher {
            return TextMatcher(text)
        }

        operator fun invoke(lexer: Lexer): TokenMatcher {
            return LexerMatcher(lexer)
        }
    }

    private class TextMatcher(private val text: String) : TokenMatcher {
        override val rule: Any = text

        override fun matches(token: Token): Boolean {
            return token.text == text
        }
    }

    private class LexerMatcher(private val lexer: Lexer) : TokenMatcher {
        override val rule: Any = lexer

        override fun matches(token: Token): Boolean {
            return token.lexer == lexer
        }
    }
}