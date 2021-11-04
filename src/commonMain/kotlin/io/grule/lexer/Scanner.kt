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
    val L_upper get() = L - ('A'..'Z')
    val L_lower get() = L - ('a'..'z')
    val L_letter get() = L + L_upper or L_lower
    val L_word get() = L + L_letter or L_digit or L + '_'
    val L_space get() = L - "\t "
    val L_wrap get() = L + "\r\n" or L - "\r\n"
    val L_eof get() = Lexer.EOF

    abstract fun scan(charStream: CharStream, tokenStream: TokenStream)

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
}
