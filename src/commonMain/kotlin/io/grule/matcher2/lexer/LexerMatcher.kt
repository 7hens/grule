package io.grule.matcher2.lexer

import io.grule.matcher2.Matcher

typealias LexerMatcher = Matcher<LexerMatcherStatus>

typealias LexerSupplier = LexerMatcherDsl.() -> LexerMatcher

operator fun LexerMatcher.plus(text: String): LexerMatcher {
    return plus(LexerMatcherString(text))
}

operator fun LexerMatcher.minus(charSet: Iterable<Char>): LexerMatcher {
    return plus(LexerMatcherCharSet(charSet))
}

operator fun LexerMatcher.plus(char: Char): LexerMatcher {
    return minus(listOf(char))
}

operator fun LexerMatcher.minus(charArray: CharArray): LexerMatcher {
    return minus(charArray.toList())
}

operator fun LexerMatcher.minus(text: String): LexerMatcher {
    return minus(text.toList())
}
