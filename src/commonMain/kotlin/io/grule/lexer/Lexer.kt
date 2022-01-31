package io.grule.lexer

abstract class Lexer {
    abstract fun match(charStream: CharStream, offset: Int = 0): Int

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

    fun until(terminal: Lexer, mode: UntilMode = UntilMode.GREEDY): Lexer {
        return when (mode) {
            UntilMode.GREEDY -> LexerUntilGreedy(this, terminal)
            UntilMode.RELUCTANT -> LexerUntilReluctant(this, terminal)
        }
    }

    companion object {
        val EOF: Lexer = LexerEOF
    }

    enum class UntilMode { GREEDY, RELUCTANT }
}
