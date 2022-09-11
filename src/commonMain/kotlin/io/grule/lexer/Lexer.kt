package io.grule.lexer

import io.grule.token.CharStream
import io.grule.token.TokenStream
import io.grule.token.TokenStreamImpl

@Suppress("MemberVisibilityCanBePrivate")
interface Lexer {
    fun lex(context: LexerContext)

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    fun tokenStream(text: String): TokenStream {
        return tokenStream(CharStream.fromString(text))
    }

    companion object {
        val EOF: Lexer = LexerEOF

        fun indent(newLine: Lexer, indent: Lexer, dedent: Lexer): Lexer {
            return LexerIndent(newLine, indent, dedent)
        }

        fun factory(): LexerFactory2 {
            return LexerFactory2()
        }
    }
}
