package io.grule.lexer

import io.grule.matcher.CharStream
import io.grule.matcher.Matcher

@Suppress("PropertyName", "MemberVisibilityCanBePrivate")
fun interface Lexer {
    fun lex(context: LexerContext)

    fun tokenStream(charStream: CharStream): TokenStream {
        return TokenStreamImpl(charStream, this)
    }

    companion object {
        val EOF: Lexer = LexerEOF

        fun of(matcher: Matcher, emitsToken: Boolean = true): Lexer {
            return LexerMatcher(matcher, emitsToken)
        }

        fun indent(newLine: Lexer, indent: Lexer, dedent: Lexer): Lexer {
            return LexerIndent(newLine, indent, dedent)
        }

        fun factory(): LexerFactory {
            return LexerFactory()
        }
    }
}
