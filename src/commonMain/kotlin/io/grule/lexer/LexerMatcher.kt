package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.token.CharStream

typealias LexerMatcher = Matcher<LexerStatus>

typealias LexerSupplier = LexerDsl.() -> LexerMatcher

interface LexerMatcherExt {
    operator fun LexerMatcher.plus(text: String): LexerMatcher {
        return plus(LexerMatcherString(text))
    }

    operator fun LexerMatcher.minus(charSet: Iterable<Char>): LexerMatcher {
        return plus(LexerMatcherCharSet(charSet))
    }

    operator fun LexerMatcher.minus(char: Char): LexerMatcher {
        return minus(listOf(char))
    }

    operator fun LexerMatcher.minus(charArray: CharArray): LexerMatcher {
        return minus(charArray.toList())
    }

    operator fun LexerMatcher.minus(text: String): LexerMatcher {
        return minus(text.toList())
    }

    operator fun LexerMatcher.div(text: String): LexerMatcher {
        return plus(LexerMatcherRegex(text))
    }

    fun LexerMatcher.match(charStream: CharStream, offset: Int = 0): Int {
        val status = LexerStatus(charStream, charStream.charIndex + offset)
        return match(status).position - status.position
    }
}