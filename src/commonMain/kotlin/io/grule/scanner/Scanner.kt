package io.grule.scanner

import io.grule.lexer.CharStream
import io.grule.lexer.Lexer
import io.grule.lexer.LexerCharSet
import io.grule.parser.AstNode
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class Scanner : ReadOnlyProperty<Any?, Scanner> {
    val P: Parser get() = ParserBuilder()

    abstract fun scan(context: ScannerContext)

    var name = "__" + this::class.simpleName

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

    fun parse(parser: Parser, text: String): AstNode {
        return parse(parser, CharStream.fromString(text))
    }
}
