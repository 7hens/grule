package io.grule.lexer

abstract class Lexer {
    abstract fun match(charStream: CharStream, offset: Int = 0): Int

    open fun not(): Lexer {
        return LexerNot(this)
    }

    open operator fun plus(lexer: Lexer): Lexer {
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
        require(minTimes <= maxTimes)
        require(minTimes >= 0)
        return LexerRepeat(this, minTimes, maxTimes)
    }

    fun join(separator: Lexer, minTimes: Int = 0, maxTimes: Int = Int.MAX_VALUE): Lexer {
        val min = maxOf(minTimes - 1, 0)
        val max = maxOf(maxTimes - 1, 0)
        val item = LexerBuilder() + this + separator
        val lexer = LexerBuilder() + item.repeat(min, max) + this
        return if (minTimes == 0) lexer.optional() else lexer
    }

    fun optional(): Lexer {
        return repeat(0, 1)
    }

    fun optional(isOptional: Boolean): Lexer {
        return if (isOptional) optional() else this
    }

    fun interlace(separator: Lexer): Lexer {
        return LexerBuilder() + separator.optional() + join(separator) + separator.optional()
    }

    fun unless(terminal: Lexer): Lexer {
        return LexerUnless(this, terminal)
    }

    fun until(terminal: Lexer): Lexer {
        return LexerUntil(this, terminal)
    }

    companion object {
        val EOF: Lexer = LexerEOF
    }
}
