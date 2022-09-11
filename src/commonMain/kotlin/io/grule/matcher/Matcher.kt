package io.grule.matcher

import io.grule.token.MatcherContext

@Suppress("MemberVisibilityCanBePrivate")
fun interface Matcher {
    fun match(context: MatcherContext, offset: Int): Int

    fun match(context: MatcherContext) = match(context, 0)

    fun not(): Matcher {
        return MatcherNot(this)
    }

    operator fun plus(matcher: Matcher): Matcher {
        return MatcherPlus(mutableListOf(this, matcher))
    }

    operator fun plus(text: String): Matcher {
        return plus(MatcherString(text))
    }

    operator fun plus(char: Char): Matcher {
        return minus(listOf(char))
    }

    operator fun minus(charSet: Iterable<Char>): Matcher {
        return plus(MatcherCharSet(charSet))
    }

    operator fun minus(charArray: CharArray): Matcher {
        return minus(charArray.toList())
    }

    operator fun minus(text: String): Matcher {
        return minus(text.toList())
    }
//
//    operator fun div(text: String): Matcher {
//        return plus(LexerMatcherRegex(text))
//    }

    infix fun or(matcher: Matcher): Matcher {
        return MatcherOr(mutableListOf(this, matcher))
    }

    fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher {
        return MatcherRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Matcher, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        return (MatcherBuilder() + this + separator).untilGreedy(this, min, max)
    }

    fun optional(): Matcher {
        return repeat(0, 1)
    }

    fun interlace(separator: Matcher): Matcher {
        return MatcherBuilder() + separator.optional() + join(separator) + separator.optional()
    }

    fun untilGreedy(terminal: Matcher, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher {
        return MatcherUntilGreedy(this, terminal, minTimes, maxTimes)
    }

    fun untilNonGreedy(terminal: Matcher, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Matcher {
        return MatcherUntilNonGreedy(this, terminal, minTimes, maxTimes)
    }

    fun test(): Matcher {
        return MatcherTest(this)
    }

    companion object {
        val X: Matcher get() = MatcherShadow()
        val ANY: Matcher = MatcherCharSet.ANY
        val EOF: Matcher = MatcherEOF

        val DIGIT = X - ('0'..'9')
        val BIT = X - "01"
        val OCTAL = X - ('0'..'7')
        val HEX = X - "0123456789ABCDEFabcdef"
        val UPPER = X - ('A'..'Z')
        val LOWER = X - ('a'..'z')
        val LETTER = UPPER or LOWER
        val WORD = LETTER or DIGIT or X + '_'
        val WORD_HEAD = LETTER or X + '_'
        val WORD_LIST = WORD_HEAD + WORD.repeat()
        val DWORD = WORD or X + '$'
        val DWORD_HEAD = LETTER or X - "_$"
        val DWORD_LIST = DWORD_HEAD + DWORD.repeat()
        val SPACE = X - "\t\r\n\u0085\u000B\u000C "
        val WRAP = X + "\r\n" or X - "\r\n"

        operator fun div(fn: Companion.() -> Matcher): Matcher {
            return fn(Matcher)
        }
    }
}
