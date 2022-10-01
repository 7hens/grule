package io.grule.lexer

import io.grule.matcher.Matcher
import io.grule.token.CharStream

typealias LexerMatcher = Matcher<LexerStatus>

typealias LexerSupplier = LexerDsl.() -> LexerMatcher

interface LexerMatcherExt {

    operator fun LexerMatcher.get(charSet: Iterable<Char>): LexerMatcher {
        return plus(LexerMatcherCharSet(charSet))
    }

    operator fun LexerMatcher.get(char: Char): LexerMatcher {
        return get(listOf(char))
    }

    operator fun LexerMatcher.get(charArray: CharArray): LexerMatcher {
        return get(charArray.toList())
    }

    operator fun LexerMatcher.get(text: String): LexerMatcher {
        return get(text.toList())
    }

    operator fun LexerMatcher.div(text: String): LexerMatcher {
        return plus(LexerMatcherRegex(text))
    }

    operator fun LexerMatcher.plus(text: String): LexerMatcher {
        return plus(LexerMatcherString(text))
    }

    @Deprecated("Uses [charSet] instead", ReplaceWith("X[charSet]"))
    operator fun LexerMatcher.minus(charSet: Iterable<Char>): LexerMatcher {
        return get(charSet)
    }

    @Deprecated("Uses [char] instead", ReplaceWith("X[char]"))
    operator fun LexerMatcher.minus(char: Char): LexerMatcher {
        return get(char)
    }

    @Deprecated("Uses [charArray] instead", ReplaceWith("X[charArray]"))
    operator fun LexerMatcher.minus(charArray: CharArray): LexerMatcher {
        return get(charArray)
    }

    @Deprecated("Uses [text] instead", ReplaceWith("X[text]"))
    operator fun LexerMatcher.minus(text: String): LexerMatcher {
        return get(text)
    }
    
    fun LexerMatcher.match(charStream: CharStream, offset: Int = 0): Int {
        val status = LexerStatus(charStream, charStream.charIndex + offset)
        return match(status).position - status.position
    }
}