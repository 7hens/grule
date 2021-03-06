package io.grule.lexer

import io.grule.parser.AstNode
import io.grule.parser.Parser
import io.grule.parser.ParserBuilder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
abstract class Scanner : ReadOnlyProperty<Any?, Scanner> {
    val L: Lexer get() = LexerBuilder()
    val P: Parser get() = ParserBuilder()

    val L_any get() = L + LexerCharSet.ANY
    val L_digit get() = L - ('0'..'9')
    val L_bit get() = L - "01"
    val L_octal get() = L - ('0'..'7')
    val L_hex get() = L + L_digit or L - ('A'..'F') or L - ('a'..'f')
    val L_upper get() = L - ('A'..'Z')
    val L_lower get() = L - ('a'..'z')
    val L_letter get() = L + L_upper or L_lower
    val L_word get() = L + L_letter or L_digit or L + '_'
    val L_space get() = L - "\t\r\n\u0085\u000B\u000C "
    val L_wrap get() = L + "\r\n" or L - "\r\n"
    val L_eof get() = Lexer.EOF

    abstract fun scan(charStream: CharStream, tokenStream: TokenStream)

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
