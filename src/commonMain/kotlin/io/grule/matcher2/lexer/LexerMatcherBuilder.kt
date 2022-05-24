package io.grule.matcher2.lexer

import io.grule.matcher2.MatcherBuilder

class LexerMatcherBuilder : MatcherBuilder<LexerMatcherContext, LexerMatcherBuilder>() {

    operator fun plus(text: String): LexerMatcherBuilder {
        return plus(LexerMatcherString(text))
    }

    operator fun plus(char: Char): LexerMatcherBuilder {
        return minus(listOf(char))
    }

    operator fun minus(charSet: Iterable<Char>): LexerMatcherBuilder {
        return plus(LexerMatcherCharSet(charSet))
    }

    operator fun minus(charArray: CharArray): LexerMatcherBuilder {
        return minus(charArray.toList())
    }

    operator fun minus(text: String): LexerMatcherBuilder {
        return minus(text.toList())
    }
}