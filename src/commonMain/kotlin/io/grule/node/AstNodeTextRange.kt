package io.grule.node

import io.grule.token.TextPosition
import io.grule.token.TextRange
import io.grule.token.Token

internal class AstNodeTextRange(val node: AstNode) : TextRange {

    override val start: TextPosition by lazy { node.tokens.firstPosition { textRange.start } }

    override val end: TextPosition by lazy { node.reversedTokens().firstPosition { textRange.end } }

    private fun AstNode.reversedTokens(): Sequence<Token> {
        if (isTerminal()) {
            return tokens
        }
        return children.reversed().asSequence().flatMap { it.reversedTokens() }
    }

    private fun Sequence<Token>.firstPosition(fn: Token.() -> TextPosition): TextPosition {
        return firstOrNull()?.fn() ?: TextPosition.Default
    }
}