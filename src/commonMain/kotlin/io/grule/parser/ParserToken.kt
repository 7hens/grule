package io.grule.parser

import io.grule.lexer.TokenStream

internal class ParserToken(private val matcher: TokenMatcher) : Parser() {
    override val isFlatten = true

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val token = tokenStream.peek(offset)
        if (matcher.matches(token)) {
            parentNode.add(matcher.node(token))
            return 1
        }
        throw ParserException("Unmatched token matcher (${matcher.rule}), " +
                "actual is (${token.scanner}) \"${token.text}\" #${token.line}:${token.column}")
    }
}