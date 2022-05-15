package io.grule.parser

import io.grule.scanner.TokenStream

internal class ParserToken(private val matcher: TokenMatcher) : Parser() {

    override fun parse(tokenStream: TokenStream, offset: Int, parentNode: AstNode): Int {
        val token = tokenStream.peek(offset)
        if (matcher.matches(token)) {
            parentNode.add(matcher.node(token))
            return 1
        }
        throw ParserException("Unmatched token matcher ${matcher.rule} in ${parentNode.key}, " +
                "actual is $token")
    }
}