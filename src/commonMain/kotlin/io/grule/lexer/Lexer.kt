package io.grule.lexer

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Lexer : ReadOnlyProperty<Any?, Lexer> {
    abstract fun match(charStream: CharStream, offset: Int): Int
    
    open fun parse(tokenStream: TokenStream, offset: Int = 0): Int {
        return match(tokenStream.charStream, offset)
    }

    var name = "$" + this::class.simpleName

    override fun getValue(thisRef: Any?, property: KProperty<*>): Lexer {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

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

    operator fun minus(fn: TokenStream.(Int) -> Unit): Lexer {
        return LexerAction(this, fn)
    }

    fun token(): Lexer {
        return minus { num ->
            emit(this@Lexer, charStream.getText(0, num))
            charStream.moveNext(num)
        }
    }

    fun skip(): Lexer {
        return minus { charStream.moveNext(it) }
    }

    companion object {
        val EOF: Lexer = LexerEOF
    }
}
