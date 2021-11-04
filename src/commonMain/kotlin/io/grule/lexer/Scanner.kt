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

    val ANY get() = L + LexerCharSet.ANY
    val DIGIT get() = L - ('0'..'9')
    val UPPER_CASE get() = L - ('A'..'Z')
    val LOWER_CASE get() = L - ('a'..'z')
    val LETTER get() = L + UPPER_CASE or LOWER_CASE
    val WORD get() = L + LETTER or DIGIT or L + '_'
    val SPACE get() = L - "\t "
    val LINE get() = L + "\r\n" or L - "\r\n"
    val EOF get() = Lexer.EOF

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
}
