package io.grule.lexer

import io.grule.matcher.CharStream
import io.grule.matcher.Matcher
import io.grule.matcher.MatcherCharSet
import io.grule.parser.AstNode
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class Lexer : ReadOnlyProperty<Any?, Lexer> {
    val P: Parser get() = ParserBuilder()

    abstract fun lex(context: LexerContext)

    var name = "__" + this::class.simpleName

    override fun getValue(thisRef: Any?, property: KProperty<*>): Lexer {
        name = property.name
        return this
    }

    override fun toString(): String {
        return name
    }

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    operator fun String.unaryMinus(): Matcher {
        return MatcherCharSet(toList())
    }

    fun parse(parser: Parser, charStream: CharStream): AstNode {
        return parser.parse(tokenStream(charStream))
    }

    fun parse(parser: Parser, text: String): AstNode {
        return parse(parser, CharStream.fromString(text))
    }
}
