package io.grule.lexer

import io.grule.parser.AstNode
import io.grule.parser.Parser
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class Scanner : ReadOnlyProperty<Any?, Scanner> {
    abstract fun scan(tokenStream: TokenStream)

    var name = "$" + this::class.simpleName

    override fun getValue(thisRef: Any?, property: KProperty<*>): Scanner {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    operator fun String.unaryMinus(): Lexer {
        return LexerCharSet(toList())
    }

    fun parse(parser: Parser, charStream: CharStream): AstNode {
        return parser.parse(tokenStream(charStream))
    }

    companion object {
        val EOF = create(Lexer.EOF) { emitEOF() }
        val EMPTY = object : Scanner() {
            override fun scan(tokenStream: TokenStream) {
            }
        }

        fun skip(lexer: Lexer): Scanner {
            return object : Scanner() {
                override fun scan(tokenStream: TokenStream) {
                    val charStream = tokenStream.charStream
                    val matchNum = lexer.match(charStream)
                    charStream.moveNext(matchNum)
                }
            }
        }

        fun token(lexer: Lexer): Scanner {
            return object : Scanner() {
                override fun scan(tokenStream: TokenStream) {
                    val charStream = tokenStream.charStream
                    val matchNum = lexer.match(charStream)
                    tokenStream.emit(this, charStream.getText(0, matchNum))
                    charStream.moveNext(matchNum)
                }
            }
        }

        fun create(lexer: Lexer, fn: TokenStream.(Int) -> Unit): Scanner {
            return object : Scanner() {
                override fun scan(tokenStream: TokenStream) {
                    val charStream = tokenStream.charStream
                    val matchNum = lexer.match(charStream)
                    fn(tokenStream, matchNum)
                }
            }
        }
    }
}
