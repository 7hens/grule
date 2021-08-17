package io.grule.parser

import io.grule.lexer.TokenChannel

internal class ParserToken(private val matcher: TokenMatcher) : Parser() {
    override val isFlatten = true

    override fun parse(channel: TokenChannel, offset: Int, parentNode: AstNode): Int {
        val token = channel.peek(offset)
        if (matcher.matches(token)) {
            parentNode.add(matcher.node(token))
            return 1
        }
        throw ParserException("Unmatched token matcher (${matcher.rule}), " +
                "actual is (${token.lexer}) \"${token.text}\" #${token.line}:${token.column}")
    }
}