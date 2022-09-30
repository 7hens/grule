package io.grule.lexer

import io.grule.node.KeyOwner
import io.grule.token.CharStream
import io.grule.token.TokenStream
import io.grule.token.TokenStreamImpl

@Suppress("MemberVisibilityCanBePrivate")
interface Lexer : LexerMatcherExt, KeyOwner {
    override val key: Any get() = this

    fun lex(context: LexerContext)

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    fun tokenStream(text: String): TokenStream {
        return tokenStream(CharStream.fromString(text))
    }

    companion object {
        val EOF: Lexer = LexerEof

        fun indent(newLine: Lexer, indent: Lexer, dedent: Lexer): Lexer {
            return LexerIndent(newLine, indent, dedent)
        }

        fun factory(): LexerFactory {
            return LexerFactory()
        }
    }
}
