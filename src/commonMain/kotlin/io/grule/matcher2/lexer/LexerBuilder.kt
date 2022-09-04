package io.grule.matcher2.lexer

class LexerBuilder {

    operator fun plus(text: String): LexerMatcher {
        return LexerMatcherString(text)
    }

    operator fun plus(char: Char): LexerMatcher {
        return minus(listOf(char))
    }

    operator fun minus(charSet: Iterable<Char>): LexerMatcher {
        return LexerMatcherCharSet(charSet)
    }

    operator fun minus(charArray: CharArray): LexerMatcher {
        return minus(charArray.toList())
    }

    operator fun minus(text: String): LexerMatcher {
        return minus(text.toList())
    }
}