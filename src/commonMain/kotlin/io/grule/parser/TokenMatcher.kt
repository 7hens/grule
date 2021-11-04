package io.grule.parser

import io.grule.lexer.Scanner
import io.grule.lexer.Token

interface TokenMatcher {
    val rule: Any

    fun matches(token: Token): Boolean

    fun node(token: Token): AstNode {
        return AstNode.Terminal(rule, token)
    }

    companion object {
        operator fun invoke(text: String): TokenMatcher {
            return TextMatcher(text)
        }

        operator fun invoke(scanner: Scanner): TokenMatcher {
            return LexerMatcher(scanner)
        }
    }

    private class TextMatcher(private val text: String) : TokenMatcher {
        override val rule: Any = text

        override fun matches(token: Token): Boolean {
            return token.text == text
        }
    }

    private class LexerMatcher(private val scanner: Scanner) : TokenMatcher {
        override val rule: Any = scanner

        override fun matches(token: Token): Boolean {
            return token.scanner == scanner
        }
    }
}