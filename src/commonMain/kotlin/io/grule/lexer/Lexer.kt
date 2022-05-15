package io.grule.lexer

@Suppress("MemberVisibilityCanBePrivate")
fun interface Lexer {
    fun match(context: LexerContext, offset: Int): Int

    fun match(context: LexerContext) = match(context, 0)

    fun not(): Lexer {
        return LexerNot(this)
    }

    operator fun plus(lexer: Lexer): Lexer {
        return LexerPlus(mutableListOf(this, lexer))
    }

    operator fun plus(text: String): Lexer {
        return plus(LexerString(text))
    }

    operator fun plus(char: Char): Lexer {
        return minus(listOf(char))
    }

    operator fun minus(charSet: Iterable<Char>): Lexer {
        return plus(LexerCharSet(charSet))
    }

    operator fun minus(charArray: CharArray): Lexer {
        return minus(charArray.toList())
    }

    operator fun minus(text: String): Lexer {
        return minus(text.toList())
    }

    operator fun div(text: String): Lexer {
        return plus(LexerRegex(text))
    }

    open infix fun or(lexer: Lexer): Lexer {
        return LexerOr(mutableListOf(this, lexer))
    }

    open fun repeat(minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Lexer {
        return LexerRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Lexer, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Lexer {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        return (LexerBuilder() + this + separator).untilGreedy(this, min, max)
    }

    fun optional(): Lexer {
        return repeat(0, 1)
    }

    fun interlace(separator: Lexer): Lexer {
        return LexerBuilder() + separator.optional() + join(separator) + separator.optional()
    }

    fun untilGreedy(terminal: Lexer, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Lexer {
        return LexerUntilGreedy(this, terminal, minTimes, maxTimes)
    }

    fun untilNonGreedy(terminal: Lexer, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Lexer {
        return LexerUntilNonGreedy(this, terminal, minTimes, maxTimes)
    }

    fun test(): Lexer {
        return LexerTest(this)
    }

    companion object {
        val X: Lexer = LexerShadow
        val ANY: Lexer = LexerCharSet.ANY
        val EOF: Lexer = LexerEOF

        val DIGIT = X - ('0'..'9')
        val BIT = X - "01"
        val OCTAL = X - ('0'..'7')
        val HEX = X - "0123456789ABCDEFabcdef"
        val UPPER = X - ('A'..'Z')
        val LOWER = X - ('a'..'z')
        val LETTER = UPPER or LOWER
        val WORD = LETTER or DIGIT or X + '_'
        val SPACE = X - "\t\r\n\u0085\u000B\u000C "
        val WRAP = X + "\r\n" or X - "\r\n"
    }
}
