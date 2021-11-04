package io.grule.lexer

abstract class Lexer {
    abstract fun match(charStream: CharStream, offset: Int = 0): Int

    open operator fun plus(lexer: Lexer): Lexer {
        return LexerPlus(mutableListOf(this, lexer))
    }

    operator fun plus(text: String): Lexer {
        return plus(LexerString(text))
    }

    operator fun plus(charSet: Iterable<Char>): Lexer {
        return plus(LexerCharSet(charSet))
    }

    operator fun plus(char: Char): Lexer {
        return plus(listOf(char))
    }

    operator fun plus(charArray: CharArray): Lexer {
        return plus(charArray.toList())
    }

    open infix fun or(lexer: Lexer): Lexer {
        return LexerOr(mutableListOf(this, lexer))
    }

    open fun not(): Lexer {
        return LexerNot(this)
    }

    open fun repeat(min: Int = 0, max: Int = Int.MAX_VALUE): Lexer {
        require(max >= min)
        return LexerRepeat(this, min, max)
    }

    fun optional(): Lexer {
        return repeat(0, 1)
    }

    open fun until(min: Int, last: Lexer): Lexer {
        return LexerUntil(this, min, last)
    }

    fun until(last: Lexer): Lexer {
        return until(0, last)
    }

    companion object {
        val EOF: Lexer = LexerEOF
    }
}
